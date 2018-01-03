import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;

//change
public class DataLinking {
	
	
	public static void main(String[] args)
	{
		String file=args[0];
		String directory=args[1];
		Dataset d=TDBFactory.createDataset(directory);
		//
		
		
	}
}
