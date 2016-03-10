package gr.agroknow.cimmyt;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;
import uiuc.oai.OaiUtil;

public class CimmytRecord extends OAIRecord  {

	public Element getMetadata() throws OAIException {
        Element ret = null;
        
        //System.out.println("I got in here!");
        
        this.priCheckIdOnly();
        try {
            Namespace oains = Namespace.getNamespace((String)"oai", (String)"http://www.openarchives.org/OAI/2.0/");
 		   	Namespace dcns = Namespace.getNamespace((String)"dc", (String)"http://purl.org/dc/elements/1.1/");
 		   	Namespace oaidcns = Namespace.getNamespace((String)"oai_dc", (String)"http://www.openarchives.org/OAI/2.0/oai_dc/");
 		   
            Vector<Namespace> nsVector = new Vector<Namespace>();
            nsVector.add(oains);
            nsVector.add(dcns);
            nsVector.add(oaidcns);
            
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
                     //System.out.println(resourceList.toString());

                     for (int i = 0; i < resourceList.size(); i++) 
                     {    
                    	 
                    	
                        Element resource_tag = resourceList.get(i);
                        //System.out.println("\nCurrent Element:" + resource_tag.getName());

                        if(resource_tag.getName().equals("identifier"))
                        {

                        	if(!resource_tag.getText().contains("http://"))
                        	{
                        		String content=resource_tag.getText();
                        		if(content.equals("0188-2465"))
                        			System.out.println("Content:"+content+"|SIZE:"+content.length());
                        		if(content.equals("968-6923-44-6"))
                        		{
                        			System.out.println("Content:"+content+"|SIZE:"+content.length());
                        			System.out.println("RL:"+resourceList.toString());
                        		}
                            	//System.out.println("Content:"+content+"|SIZE:"+content.length());
                            	if(content.length()==13)
                            	{
                            		//sample: 968-6923-44-6
                            		//System.out.println("ISBN");
                            		resource_tag.detach();                  	
                                	ret.addContent(new Element("isbn").setText(content));
                            	}
                            	if(content.length()==9)
                            	{
                            		//sample: 0188-2465
                            		if(content.equals("0188-2465"))
                            			System.out.println("Content:"+content+"|SIZE:"+content.length());
                            		//System.out.println("ISSN");
                            		resource_tag.detach();                  	
                                	ret.addContent(new Element("issn").setText(content));
                            	}
                            	continue;
                        	}
                        	//sample: http://hdl.handle.net/11529/10217
                        	
                        	if(!resource_tag.getText().contains("http://hdl.handle.net"))
                        	{
                        		if(resource_tag.getText().contains(".pdf"))
                        		{
                        			ret.addContent(new Element("pdf").setText("true"));
                        		}
                        		continue;
                        	}
                        	String url=resource_tag.getText().replace("http://", "");
                        	String[] url_array=url.split("/");
                        	
                        	try
                        	{
	                        	String domain_id=url_array[1];
	                        	String doc_id=url_array[2];

	                        	ret.addContent(new Element("domainid").setText(domain_id));
	                        	ret.addContent(new Element("cdocid").setText(doc_id));
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
                        	resource_tag.detach();
                        	descr=descr.replace("Citation: ","");                        	
                        	ret.addContent(new Element("citation").setText(descr));
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
                        	    System.out.println(matcher.group(0));
                        	    String doi=matcher.group(0).replace("doi:","http://dx.doi.org/");
                        	    ret.addContent(new Element("doi").setText(doi));
                        	}                     	
                        	
                        }
                                  		
                     }
                     for(int i=0;i<sets.size();i++)
                    	 ret.addContent(new Element("set").setText(sets.get(i)));
            }
        }
        catch (JDOMException e) {
            throw new OAIException(14, e.getMessage());
        }
        
        
        
        return ret;
    }
	
}
