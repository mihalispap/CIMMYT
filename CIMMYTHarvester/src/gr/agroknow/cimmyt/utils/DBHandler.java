package gr.agroknow.cimmyt.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jdom.JDOMException;

import gr.agroknow.cimmyt.CimmytSet;
import gr.agroknow.metadata.harvester.HarvestAllDateProcess;
import gr.agroknow.metadata.harvester.HarvestAllProcess;
import gr.agroknow.metadata.harvester.HarvestSet;
import uiuc.oai.OAIException;

public class DBHandler {
	
	private List<CimmytSet> sets=new ArrayList<CimmytSet>();
	
	/*
	 * Reads from filename and populates sets
	 * */
	public DBHandler(String filename)
	{
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
		{
			CimmytSet set;//=new CimmytSet();
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	//System.out.println(line);
		    	
		    	String[] split = line.split(",");
		    	set=new CimmytSet();
		    	set.setLastIndexed(split[1]);
		    	set.setSetSpec(split[0]);
		    	
		    	//System.out.println("SETLI:"+set.getLastIndexed()+"|SETSS:"+set.getSetSpec()+"||"+split[0]);
		    	
		    	sets.add(set);
		    	//break;
		    	//this.addSet(set);
		    	
		    	/*
		    	 * TODO:
		    	 * 		update list of sets from file
		    	 * 
		    	 * 
		    	 * */
		    }
		}
		catch(IOException e)
		{
			System.out.println("File not found, first time harvesting?");
		}
		System.out.println("Finished processing");
	}
	
	public void addSet(CimmytSet set)
	{
		int i;
		for(i=0;i<sets.size();i++)
		{
			//System.out.println("Comparing:"+sets.get(i).getSetSpec()+"|with:"+set.getSetSpec());
			if(sets.get(i).getSetSpec().equals(set.getSetSpec()))
			{
				sets.get(i).setSetName(set.getSetName());
				//sets.get(i).setSetName(set.getSetName());
				//System.out.println("Got in for:"+set.getSetSpec());
				
				break;
			}
		}
		
		if(i==sets.size())
		{
			set.setLastIndexed("1000-01-01");
			sets.add(set);
		}
	}

	public void writeToFile(String filename)
	{
		if(1==1)
			return;
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");

			String line;
			for(int i=0;i<sets.size();i++)
			{
				line="";
				line=sets.get(i).getSetSpec()+","+sets.get(i).getLastIndexed();
				//System.out.println(line);
				writer.println(line);
			}			
			
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Filenot found exception, why?");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unsupported format exception, non UTF-8?!");
		}
	}
	
	public void harvestSets(String target, String folderName, String metadataPrefix)
	{
		/*HarvestAllDateProcess example request: 
		 * param1(target) param2(foldername) param3(metadataPrefix) param4(untilD) param5(fromD) param6(Set)
		 */
		
		HarvestAllDateProcess harvest_set=new HarvestAllDateProcess();
		String[] args = new String[6];
		
		args[0]=target;//"http://repository.cimmyt.org/oai/request";//target;
		args[1]=folderName;
		args[2]=metadataPrefix; 
		
		if(target.contains("data.cimmyt") || target.contains("knowledgecenter.cimmyt"))
		{
			HarvestAllProcess harvest_all=new HarvestAllProcess();
			try {
				harvest_all.run(args);
			} catch (OAIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		 * TODO: 
		 * 	try to catch long waits
		 * 
		 * */
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");	
		
		Calendar calendar = new GregorianCalendar();
			
		//subtract 10 days
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		System.out.println("Date : " + sdf.format(calendar.getTime()));
		
		String until=sdf.format(calendar.getTime());
		
		for(int i=0;i<sets.size();i++)
		{
			args[4]=sets.get(i).getLastIndexed();
			
			
			
			args[3]=until;//"2012-01-01";	//TODO: change until yesterday!
			args[5]=sets.get(i).getSetSpec();
			
			//args[5]="2";
			//args[4]="2016-04-17";
			
			try {
				int norecords=harvest_set.run(args);
				
				/*TODO: to think about it..*/
				if(norecords!=0)
					sets.get(i).setLastIndexed(until);

			} catch (OAIException | IOException | JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//break;
			//args[0]=target;
			//if(i==1)
			//	break;
			
		}
	}
	
}




















