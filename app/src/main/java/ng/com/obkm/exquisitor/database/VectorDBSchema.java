package ng.com.obkm.exquisitor.database;

public class VectorDBSchema {

    public static final class VectorTable {
        public static final String NAME = "Vectors";

        public static final class Cols {
            public static final String PATH = "path";
            public static final String LABEL1 = "label1";
            public static final String PROB1 = "prob1";
            public static final String LABEL2 = "label2";
            public static final String PROB2 = "prob2";
            public static final String LABEL3 = "label3";
            public static final String PROB3 = "prob3";
        }
    }
}
