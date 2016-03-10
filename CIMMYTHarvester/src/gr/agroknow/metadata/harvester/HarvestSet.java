package gr.agroknow.metadata.harvester;



import java.io.File;
import java.io.IOException;
import java.util.List;

import gr.agroknow.cimmyt.CimmytRecord;
import gr.agroknow.cimmyt.CimmytRecordList;
import gr.agroknow.cimmyt.CimmytRepository;
import gr.agroknow.metadata.harvester.Record;
import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.JDomUtils;
import org.ariadne.util.OaiUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;


import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;


public class HarvestSet{
    	
       public static void run(String[] args) throws OAIException, IOException, JDOMException {
           
           if (args.length != 4) {
                System.err.println("Usage1: java HarvestProcess param1(target) param2(foldername) param3(metadataPrefix) param4(setSpec), e.g");
                System.exit(1);
            }          
           listRecords(args[0],args[1],args[2], args[3]);           
        
        //   listRecords("http://jme.collections.natural-europe.eu/oai/","C:/testSet","oai_dc","");
           /*
            * "https://openknowledge.worldbank.org/oai/request" C:\Users\Mihalis\Desktop\worldbank\  "oai_dc" AgroKnow
            * 
            * "https://cgspace.cgiar.org/oai/request" C:\Users\Mihalis\Desktop\QT1\  "oai_dc" com_10568_1
            * 
            * "https://cgspace.cgiar.org/oai/request" C:\Users\Mihalis\Desktop\QT1\  "oai_dc" com_10568_1
            * 
            * */
        }

       



	public static void listRecords(String target, String folderName, String metadataPrefix, String setSpec) throws OAIException,IOException, JDOMException {



		CimmytRepository repos = new CimmytRepository();
		File file = new File(folderName);
                String identifier = "";
		file.mkdirs();

		repos.setBaseURL(target);
		CimmytRecordList records;

		records = repos.listAllRecords(metadataPrefix,setSpec);
        int counter = 0;
		//		records.moveNext();
		while (records.moreItems()) {
			counter++;
			CimmytRecord item = records.getCurrentItem();

			//item.getMetadata();
			/*get the lom metadata : item.getMetadata();
			 * this return a Node which contains the lom metadata.
			 */
			
			//System.out.println("Here1?");
			if(!item.deleted()) {
				//System.out.println("Here2?");
				Element metadata = item.getMetadata();
				//System.out.println("Here3?");
				if(metadata != null) {
					//System.out.println(item.getIdentifier());
					Record rec = new Record();
					rec.setOaiRecord(item);
					//System.out.println("Here4?");
					rec.setMetadata(item.getMetadata());
					//System.out.println("Here5?");
					rec.setOaiIdentifier(item.getIdentifier());
                                        identifier = item.getIdentifier().replaceAll(":", "_");
                                        identifier = identifier.replaceAll("/",".");
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(metadata), 
							folderName + "/" + identifier +".xml");

				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}
                        else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			records.moveNext();
		}
		System.out.println(counter);
	}


}
