

public class TestApproach2{

	public static void main(String[] args) {
       HashTagPrediction predictor = new HashTagPrediction(); 
       //TODO set paths
       predictor.setDeliminator("###"); 
       predictor.train(); 
       //ModelHelp.printTHFM(predictor.thfm); 
       //ModelHelp.printHFM(predictor.hfm); 
       ModelHelp.printIDF(predictor.idf); 
    }
}