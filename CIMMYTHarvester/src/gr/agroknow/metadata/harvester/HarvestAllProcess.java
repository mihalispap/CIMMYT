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

//import uiuc.oai.OAIRecordList;
//import uiuc.oai.OAIRepository;


public class HarvestAllProcess{
    	
        public int run(String[] args) throws OAIException, IOException, JDOMException {
           
            
            System.out.println("Provided args:"+args.length);
            if (args.length < 3) {
                System.err.println("Usage2: java HarvestProcess param1(target) param2(foldername) param3(metadataPrefix), e.g");                
              //System.exit(1);
                return -1;
            } 
//            else{ throw new IOException("ERRROR");}    
            
            //"http://unfao.koha-ptfs.eu/cgi-bin/koha/oai.pl" C:\Users\Mihalis\Desktop\XF0_2\  "oai_dc"
            //"https://openknowledge.worldbank.org/oai/request" C:\Users\Mihalis\Desktop\agroknow\  "oai_dc"
            //"http://ageconsearch.umn.edu/dspace-oai/request" C:\Users\Mihalis\Desktop\agecon\  "oai_dc"
            //"http://data.cimmyt.org/dvn/OAIHandler" C:\Users\Mihalis\Desktop\cimmyt\  "oai_dc"
            //"http://ageconsearch.umn.edu/dspace-oai/request" C:\Users\Mihalis\Desktop\agecon_in\  "oai_dc"
            
           return listRecords(args[0],args[1],args[2]);           
                  

        //   listRecords("http://jme.collections.natural-europe.eu/oai/","C:/testSet","oai_dc","");
        }

       



	public static int listRecords(String target, String folderName, String metadataPrefix) throws OAIException,IOException, JDOMException {



		//OAIRepository repos = new OAIRepository();
		CimmytRepository repos = new CimmytRepository();
		File file = new File(folderName);
                String identifier = "";
		file.mkdirs();

                

              repos.setBaseURL(target);
 
        //OAIRecordList records;
        CimmytRecordList records;
		//OAIRecordList records = repos.listRecords("ese","9999-12-31","2000-12-31","");
              
               records = repos.listRecords(metadataPrefix);   
		
               int counter = 0;
		//		records.moveNext();
		while (records.moreItems()) {
			counter++;
			//OAIRecord item = records.getCurrentItem();
			CimmytRecord item = records.getCurrentItem();

			/*if(!item.deleted()) {
				Element metadata = item.getMetadata();
				if(metadata != null) {
					System.out.println(item.getIdentifier());
					Record rec = new Record();
					//rec.setOaiRecord(item);
					rec.setMetadata(item.getMetadata());
					rec.setOaiIdentifier(item.getIdentifier());
                                        identifier = item.getIdentifier().replaceAll(":", "_");
                                        identifier = identifier.replaceAll("/",".");
					IOUtilsv2.writeStringToFileInEncodingUTF8(OaiUtils.parseLom2Xmlstring(metadata), folderName + "/" + identifier +".xml");

				}
				else {
					System.out.println(item.getIdentifier() + " deleted");
				}
			}
            else {
					System.out.println(item.getIdentifier() + " deleted");
            }
			records.moveNext();*/
			if(!item.deleted()) {
				Element metadata = item.getMetadata(target,folderName);
				if(metadata != null) {
					//System.out.println(item.getIdentifier());
					Record rec = new Record();
					/*TODO: uncomment if needed!*/
					rec.setOaiRecord(item);
					rec.setMetadata(item.getMetadata(target,folderName));
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
		return counter;
	}


}
