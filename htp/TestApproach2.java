

public class TestApproach2{

	public static void main(String[] args) {
       HashTagPrediction predictor = new HashTagPrediction(); 
       //TODO set paths
       predictor.setDeliminator("###"); 
       predictor.train(); 

    }
}