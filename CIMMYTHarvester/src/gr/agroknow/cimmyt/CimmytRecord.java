package gr.agroknow.cimmyt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ariadne.util.IOUtilsv2;
import org.ariadne.util.OaiUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OaiUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


public class CimmytRecord extends OAIRecord  {

	public Element getMetadata(String handler, String folderName) throws OAIException, IOException {
        Element ret = null;
        
        //System.out.println("HARVESTING:"+handler);
        
        this.priCheckIdOnly();
        try {
            Namespace oains = Namespace.getNamespace((String)"oai", (String)"http://www.openarchives.org/OAI/2.0/");
 		   	Namespace dcns = Namespace.getNamespace((String)"dc", (String)"http://purl.org/dc/elements/1.1/");
 		   	Namespace oaidcns = Namespace.getNamespace((String)"oai_dc", (String)"http://www.openarchives.org/OAI/2.0/oai_dc/");
 		   
            Vector<Namespace> nsVector = new Vector<Namespace>();
            nsVector.add(oains);
            nsVector.add(dcns);
            nsVector.add(oaidcns);
            
            String datestamp="";
            String apiid="";
            
            //System.out.println(this.xmlRecord);

            List<String> sets=new ArrayList<String>();
            Element node_t = OaiUtil.getXpathNode("//oai:header/.", nsVector, this.xmlRecord);
            if (node_t != null) 
            {
            	//System.out.println(node_t.getText());
            	node_t = (Element)node_t.clone();
            	node_t.detach();
            	Element ret_t = new Document(node_t).getRootElement();
            	List<Element> resourceList = ret_t.getChildren();
            	
            	//System.out.println(resourceList.toString());
            	//System.out.println(".........");
            	for(int i=0;i<resourceList.size();i++)
            	{
            		Element resource_tag = resourceList.get(i);
            		if(resource_tag.getName().equals("setSpec"))
                    {
            			sets.add(resource_tag.getText());
            			//System.out.println("HAVE SET");
                    }

            		if(resource_tag.getName().equals("datestamp"))
                    {
            			datestamp=resource_tag.getText();
            			//System.out.println("HAVE SET");
                    }
            		
            		if(resource_tag.getName().equals("identifier"))
                    {
            			//Sample: oai:repository.cimmyt.org:10883/1064
            			String[] values=resource_tag.getText().split("/");
				        String[] domain=values[0].split(":");
				        
				        apiid=domain[2]+"_"+values[1];
                    }

            	}
            	//System.out.println(".........");
            }
            //for(int i=0;i<spec_count;i++)
       	 	//	System.out.println("SETSPECCOUNT:"+this.getSetSpec());
       	 
            
            Element node = OaiUtil.getXpathNode("oai:metadata/*", nsVector, this.xmlRecord);
            if (node != null) {
                node = (Element)node.clone();
                node.detach();
                
                ret = new Document(node).getRootElement();

                //Document document= new Document(node);
                //System.out.println("Root element :" + ret.getName());

                     Element classElement = ret;

                     List<Element> resourceList = classElement.getChildren();
                     //System.out.println("RL:"+resourceList.toString());
                     String urlV="";
                     //System.out.println(resourceList.toString());

                     boolean flag=false;
                     String resource_title="empty";
                     for (int i = 0; i < resourceList.size(); i++) 
                     {    
                    	 
                    	
                        Element resource_tag = resourceList.get(i);

                        /*
                         * Country tagging
                         * 
                         * */
                        
                		if(resource_tag.getName().equals("subject")
                				|| resource_tag.getName().equals("description")
                				|| resource_tag.getName().equals("title"))
                		{
                			String[] value=resource_tag.getText().split(" ");
                			
                			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
            						+ "assets"+System.getProperty("file.separator");
                			
                			/*
                			 * 	TODO: 
                			 * 		rethink about case sensitive/insensitive
                			 * 
                			 * */
                			for(int j=0;j<value.length;j++)
                			{
                				value[j]=value[j].replace(",", "");
                				value[j]=value[j].replace("(", "");
                				value[j]=value[j].replace(")", "");
                				
                				FileInputStream fstream = new FileInputStream(absolute_path+"continents.db");
                				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                				String strLine;
                				while ((strLine = br.readLine()) != null)   
                				{

                					String[] geonames=strLine.split("\t");
                				  
	                				boolean found=false;
	                				String geonames_id="";

	                				if(value[j].equalsIgnoreCase(geonames[1]))
	                				{
	                						found=true;
	                						geonames_id=geonames[2];
	                				}
	                				if(found)
	                				{
	                					//if(resource_tag.getName().equals("description") || value.length>1)
	                						ret.addContent(new Element("geotag",dcns).setText(value[j]));
	                					//else
	                					//	resource_tag.setName("geotag");
	                					ret.addContent(new Element("geonames",dcns).setText(
	                							"http://sws.geonames.org/"+geonames_id));
	                					break;
	                				}
                				}
                				br.close();
                				
                				fstream = new FileInputStream(absolute_path+"countries.db");
                				br = new BufferedReader(new InputStreamReader(fstream));
                				while ((strLine = br.readLine()) != null)   
                				{

                					String[] geonames=strLine.split("\t");
                				  
	                				boolean found=false;
	                				String geonames_id="";
	                				
	                				if(value[j].equalsIgnoreCase(geonames[4]))
	                				{
	                						found=true;
	                						geonames_id=geonames[16];
	                				}
	                				if(found)
	                				{
	                					//if(resource_tag.getName().equals("description") || value.length>1)
	                						ret.addContent(new Element("geotag",dcns).setText(value[j]));
	                					//else
	                					//	resource_tag.setName("geotag");
	                					ret.addContent(new Element("geonames",dcns).setText(
	                							"http://sws.geonames.org/"+geonames_id));
	                					break;
	                				}
                				}
                				br.close();
                				
                			}
                			
                		}
                        
                		
                		/*
                		 * 
                		 * Language enrichment
                		 * 
                		 * */
                		if(resource_tag.getName().equals("language"))
                		{
                			String value=resource_tag.getText();
                			
                			String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
            						+ "assets"+System.getProperty("file.separator");
                			
                			/*
                			 * 	TODO: 
                			 * 		rethink about case sensitive/insensitive
                			 * 
                			 * */
                			
                				FileInputStream fstream = new FileInputStream(absolute_path+"iso-languagecodes.db");
                				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                				String strLine;
                				while ((strLine = br.readLine()) != null)   
                				{

                					String[] langs=strLine.split("\t");
                				  
	                				boolean found=false;

	                				if(value.equals(langs[3]))
	                				{
	                						found=true;
	                				}
	                				if(found)
	                				{
	                					resource_tag.setText(langs[0]);
	                					ret.addContent(new Element("lexvo",dcns).setText(
	                							"http://lexvo.org/id/iso639-3/"+langs[0]));
	                					break;
	                				}
                				}
                				br.close();
                        				
                			
                			
                		}
                		
                		
                        if(resource_tag.getName().equals("identifier"))
                        {

                        	
                        	if(!resource_tag.getText().contains("http://"))
                        	{
                        		String content=resource_tag.getText();
                        		
                            	//System.out.println("Content:"+content+"|SIZE:"+content.length());
                            	if(content.length()>=13)
                            	{
                            		//sample: 968-6923-44-6
                            		//resource_tag.detach();    
                            		resource_tag.setName("isbn");
                                	//ret.addContent(new Element("isbn").setText(content));
                            	}
                            	if(content.length()==9)
                            	{
                            		//sample: 0188-2465
                            		//resource_tag.detach();
                            		resource_tag.setName("issn");
                                	//ret.addContent(new Element("issn").setText(content));
                            	}
                            	continue;
                        	}
                        	//sample: http://hdl.handle.net/11529/10217
                        	
                        	if(!resource_tag.getText().contains("http://hdl.handle.net"))
                        	{
                        		if(resource_tag.getText().contains(".pdf"))
                        		{
                        			resource_tag.setName("pdf");
                        			//ret.addContent(new Element("pdf").setText("true"));
                        		}
                        		continue;
                        	}
                        	String url=resource_tag.getText().replace("http://", "");
                        	String[] url_array=url.split("/");
                        	
                        	urlV=resource_tag.getText();
                        	
                        	try
                        	{
	                        	String domain_id=url_array[1];
	                        	String doc_id=url_array[2];

	                        	ret.addContent(new Element("domainid",dcns).setText(domain_id));
	                        	ret.addContent(new Element("cdocid",dcns).setText(doc_id));
	                        	
	                        	/*Important to check that ALL resources have this!*/
	                        	ret.addContent(new Element("apiid",dcns).setText(domain_id+"_"+doc_id));
	                        	
	                        	apiid=domain_id+"_"+doc_id;
	                        	flag=true;
                        	}
                        	catch(java.lang.ArrayIndexOutOfBoundsException e)
                        	{
                        		e.printStackTrace();
                        	}                      	
                        	
                        }


                        if(resource_tag.getName().equals("description"))
                        {
                        	String descr=resource_tag.getText();
                        	
                        	if(!descr.contains("Citation: "))
                        		continue;
                        	
                        	//sample: Citation: Vargas, Mateo; Combs, Emily; [...]
                        	//resource_tag.detach();
                        	//descr=descr.replace("Citation: ","");  
                        	resource_tag.setText(resource_tag.getText().replace("Citation: ",""));
                        	resource_tag.setName("citation");
                        	//ret.addContent(new Element("citation").setText(descr));
                        }
                        
                        if(resource_tag.getName().equals("relation"))
                        {
                        	String relation=resource_tag.getText();
                        	
                        	if(!relation.contains("doi:"))
                        		continue;
                        	
                        	//sample: doi:10.2134/agronj2012.0016
                        	//TOMAKE: http://dx.doi.org/10.2134/agronj2012.0016
                        	
                        	Pattern pattern = Pattern.compile("doi:(.*?) ");
                        	Matcher matcher = pattern.matcher(relation);
                        	if (matcher.find())
                        	{
                        	    //System.out.println(matcher.group(0));
                        	    String doi=matcher.group(0).replace("doi:","http://dx.doi.org/");
                        	    ret.addContent(new Element("doi",dcns).setText(doi));
                        	}                     	
                        	
                        }

                        if(resource_tag.getName().equals("publisher"))
                        {
                        	if(resource_tag.getText().contains("http://") && resource_tag.getText().contains(".pdf"))
                        	{
                        		resource_tag.setName("pdf");
                        	}
                        }
                        

                        if(resource_tag.getName().equals("date"))
                        {
                        	if(resource_tag.getText().length()<=10)
                        	{
                        		resource_tag.setName("pubDate");
                        	}

                        	if(resource_tag.getText().length()==5 && resource_tag.getText().endsWith("."))
                        	{
                        		resource_tag.setText(resource_tag.getText().replace(".",""));
                        		resource_tag.setName("pubDate");
                        	}
                        }
                        
                        if(resource_tag.getName().equals("subject"))
                        {
                        	resource_tag.setText(resource_tag.getText().replace("DESCRIPTORS:",""));
                        	resource_tag.setName("subject");
                        	
                        }

                        if(resource_tag.getName().equals("title"))
                        {
                        	resource_title=resource_tag.getText();
                        	
                        }
                        
                        if(resource_tag.getName().equals("format"))
                        {
                        	if(resource_tag.getText().contains("dpi"))
                        	{
                        		resource_tag.setName("quality");
                        	}
                        	if(resource_tag.getText().contains("Quality"))
                        	{
                        		resource_tag.setName("quality");
                        	}
                        	if(resource_tag.getText().contains("fps"))
                        	{
                        		resource_tag.setName("quality");
                        	}
                        }
                        
                           		
                     }
                     for(int i=0;i<sets.size();i++)
                     {
                    	 ret.addContent(new Element("cset",dcns).setText(sets.get(i)));
                    	 
                    	 String setid=handler+sets.get(i);
                    	 int id=setid.hashCode();
                    	 if(id<0)
                    		 id*=-1;
                    	 ret.addContent(new Element("setid",dcns).setText(String.valueOf(id)));
                     }
                     if(!flag)
                     {
                    	 //System.out.println("INPUTING MANUAL ID!!!");
                    	 
                    	 int hash=resource_title.hashCode();
                    	 if(hash<0)
                    		 hash*=-1;
                    	 //ret.addContent(new Element("apiid",dcns).setText(String.valueOf(hash)));
                    	 ret.addContent(new Element("apiid",dcns).setText(apiid));
                    	 
                    	 String[] ddid=apiid.split("_");
                    
                     	 ret.addContent(new Element("domainid",dcns).setText(ddid[0]));
                     	 ret.addContent(new Element("cdocid",dcns).setText(ddid[1]));
                     	
                    	 
                    	 //apiid=String.valueOf(hash);
                     }
                     ret.addContent(new Element("updated_date",dcns).setText(datestamp));
                     
                     ret.addContent(new Element("handler",dcns).setText(handler));
                     /*
                     String json_string="{\"uri\":\""+apiid+"\",";
                     
                     String type="resource";
                     List<String> descrs=new ArrayList<String>();
                     List<String> langs=new ArrayList<String>();
                     
                     if(handler.contains("data.cimmyt"))
                    	 type="dataset";
                     
                     json_string+="\"type\": \""+type+"\",";
                     for (int i = 0; i < resourceList.size(); i++) 
                     {
                    	 Element resource_tag = resourceList.get(i);

                    	 if(resource_tag.getName().equals("title"))
                    	 {
                    		 json_string+="\"title\": "+org.codehaus.jettison.json.JSONObject.quote(resource_tag.getText())+",";
                    	 }
                    	 if(resource_tag.getName().equals("created_date"))
                    	 {
                    		 json_string+="\"created_date\": "+org.codehaus.jettison.json.JSONObject.quote(resource_tag.getText())+",";
                    	 }
                    	 if(resource_tag.getName().equals("description"))
                    	 {
                    		 descrs.add(resource_tag.getText());
                    	 }
                    	 if(resource_tag.getName().equals("language"))
                    	 {
                    		 langs.add(resource_tag.getText());
                    	 }
                     }

                     if(descrs.size()>0)
                     {
                    	 json_string+="\"description\":[";
                    	 for(int i=0;i<descrs.size();i++)
                    	 {
                    		 if(i!=descrs.size()-1)
                    			 json_string+=""+org.codehaus.jettison.json.JSONObject.quote(descrs.get(i))+",";
                    		 else
                    			 json_string+=""+org.codehaus.jettison.json.JSONObject.quote(descrs.get(i))+"";
                    	 }
                    	 json_string+="],";
                     }

                     if(langs.size()>0)
                     {
                    	 json_string+="\"language\":[";
                    	 for(int i=0;i<langs.size();i++)
                    	 {
                    		 if(i!=langs.size()-1)
                    			 json_string+=""+org.codehaus.jettison.json.JSONObject.quote(langs.get(i))+",";
                    		 else
                    			 json_string+=""+org.codehaus.jettison.json.JSONObject.quote(langs.get(i))+"";
                    	 }
                    	 json_string+="],";
                     }
                     
                     json_string+="}";
                     json_string=json_string.replace(",}", "}");                     
                     IOUtilsv2.writeStringToFileInEncodingUTF8(json_string,folderName + "/" +apiid +".entity.json");
                     
                     */
            }
        }
        catch (JDOMException e) {
            throw new OAIException(14, e.getMessage());
        }
        
        return ret;
    }
	
}
