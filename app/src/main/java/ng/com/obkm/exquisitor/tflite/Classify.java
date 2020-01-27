package ng.com.obkm.exquisitor.tflite;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import ng.com.obkm.exquisitor.PhotoItem;
import ng.com.obkm.exquisitor.VectorLab;

public class Classify {

    final String TAG = "classify";

    private Context mContext;

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;
    // holds the selected image data as bytes
    private ByteBuffer imgData = null;
    // holds the probabilities of each label for non-quantized graphs
    private float[][] labelProbArray = null;
    // holds the probabilities of each label for quantized graphs
    private byte[][] labelProbArrayB = null;
    // array that holds the labels with the highest probabilities
    private String[] topLabels = null;
    // array that holds the highest probabilities
    private String[] topConfidence = null;
    private float[] topConfidencef = null;

    // selected classifier information received from extras
    private String chosen = "inception_float.tflite"; // quant model is ~50% faster
    private boolean quant = false;

    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X = 299;
    private int DIM_IMG_SIZE_Y = 299;
    private int DIM_PIXEL_SIZE = 3;

    // int array to hold image data
    private int[] intValues;

    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    // constructor
    public Classify(Context context) {
        mContext = context;
    }

    public void setUpTFLite() {
        // TFLITE - from https://github.com/soum-io/TensorFlowLiteInceptionTutorial
        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        //initilize graph and labels
        try{
            Log.i(TAG, "Entered try phase");
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            Log.i(TAG, "Check tflite: ");
            labelList = loadLabelList();
        } catch (Exception ex){
            Log.i(TAG, "Entered catch phase");
            ex.printStackTrace();
        }
        // initialize byte array. The size depends if the input data needs to be quantized or not
        if(quant){
            imgData =
                   ByteBuffer.allocateDirect(
                           DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        } else {
            imgData =
                    ByteBuffer.allocateDirect(
                            4 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        }
        imgData.order(ByteOrder.nativeOrder());
        // initialize probabilities array. The datatypes that array holds depends if the input data needs to be quantized or not
        if(quant){
            labelProbArrayB= new byte[1][labelList.size()];
        } else {
            labelProbArray = new float[1][labelList.size()];
        }
        // initialize array to hold top labels
        topLabels = new String[RESULTS_TO_SHOW];
        // initialize array to hold top probabilities
        topConfidence = new String[RESULTS_TO_SHOW];
        topConfidencef = new float[RESULTS_TO_SHOW];
    }

    public void runTFLite(Bitmap imageBitmap, String path) {
        // resize the bitmap to the required input size to the CNN
        Bitmap bitmap = getResizedBitmap(imageBitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
        // convert bitmap to byte array
        convertBitmapToByteBuffer(bitmap);
        // pass byte data to the graph
        if(quant){
            tflite.run(imgData, labelProbArrayB);
        } else {
            tflite.run(imgData, labelProbArray);
            // int count = 0;
            for (int i = 0; i < labelProbArray[0].length; i++) {
                float x = labelProbArray[0][i];
                Log.i(TAG, "LabelProbArray for first image: " + x);
            }
        }
        // display the results
        getTopLabelProb(path);
    }

    // print the top labels and respective confidences
    private void getTopLabelProb(String path) {
        // add all results to priority queue
        for (int i = 0; i < labelList.size(); ++i) {
            if(quant){
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), (labelProbArrayB[0][i] & 0xff) / 255.0f));
            } else {
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            }
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        getTopResultsfromPQ(path);
    }

    private void getTopResultsfromPQ(String path){
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLabels[i] = label.getKey();
            Log.i(TAG, "LOOK " + i + label.getKey());
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
            topConfidencef[i] = label.getValue();
        }

        addLabelsAndProbsToDB(path);
    }

    private void addLabelsAndProbsToDB(String path){
        PhotoItem item = new PhotoItem(path);
        int label1 = labelList.indexOf(topLabels[0]);
        int label2 = labelList.indexOf(topLabels[1]);
        int label3 = labelList.indexOf(topLabels[2]);

        float prob1 = topConfidencef[0];
        float prob2 = topConfidencef[1];
        float prob3 = topConfidencef[2];

        // adding labels and probabilities to DB
        VectorLab.get(mContext).addVector(item.getImagePath(), label1, prob1, label2, prob2, label3, prob3);
        System.out.println("One vector added to the DB.");
    }

    // loads tflite graph from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(chosen);
//        Log.i(TAG, "Check fileDescriptor: " + (fileDescriptor == null));
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer model = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//        Log.i(TAG, "Check model: " + (model == null));
        return model;
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        Log.i(TAG, "Checking bitmap " + (bitmap == null));
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                // if quantized, convert each rgb value to a byte, otherwise to a float
                if(quant){
                    imgData.put((byte) ((val >> 16) & 0xFF));
                    imgData.put((byte) ((val >> 8) & 0xFF));
                    imgData.put((byte) (val & 0xFF));
                } else {
                    imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }
            }
        }
    }

    // loads the labels from the label txt file in assets into a string array
    private List<String> loadLabelList() throws IOException {
        Log.i(TAG, "Entered loadLabelList method");
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(mContext.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        Log.d("singlePhoto", "LabelList size: " + labelList.size());
        return labelList;
    }

    // resizes bitmap to given dimensions
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}