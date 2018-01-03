import org.apache.jena.tdb.TDBFactory;


public class DataLinking {
	
	
	public static void main(String[] args)
	{
		String file=args[0];
		String directory=args[1];
		org.apache.jena.query.Dataset d=TDBFactory.createDataset(directory);
		
		
	}
}
