package gr.agroknow.cimmyt;

import java.util.List;
import java.util.Vector;

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
        
        System.out.println("I got in here!");
        
        this.priCheckIdOnly();
        try {
            Namespace oains = Namespace.getNamespace((String)"oai", (String)"http://www.openarchives.org/OAI/2.0/");
 		   	Namespace dcns = Namespace.getNamespace((String)"dc", (String)"http://purl.org/dc/elements/1.1/");
 		   	Namespace oaidcns = Namespace.getNamespace((String)"oai_dc", (String)"http://www.openarchives.org/OAI/2.0/oai_dc/");
 		   
            Vector<Namespace> nsVector = new Vector<Namespace>();
            nsVector.add(oains);
            nsVector.add(dcns);
            nsVector.add(oaidcns);
            Element node = OaiUtil.getXpathNode("oai:metadata/*", nsVector, this.xmlRecord);
            if (node != null) {
                node = (Element)node.clone();
                node.detach();
                ret = new Document(node).getRootElement();


                System.out.println("Root element :" 
                        + ret.getName());

                     Element classElement = ret;

                     List<Element> resourceList = classElement.getChildren();
                     System.out.println("----------------------------");

                     for (int i = 0; i < resourceList.size(); i++) {    
                        Element resource_tag = resourceList.get(i);
                        System.out.println("\nCurrent Element:" 
                           + resource_tag.getName());
                        
                        if(resource_tag.getName().equals("title"))
                        	System.out.println("TITLE:"+resource_tag.getText());
                        
                        /*Attribute attribute =  student.getAttribute("rollno");
                        System.out.println("Student roll no : " 
                           + attribute.getValue() );*/
                        /*System.out.println("First Name : " + student.getChild("firstname").getText());
                        System.out.println("Last Name : "+ student.getChild("lastname").getText());
                        System.out.println("Nick Name : "+ student.getChild("nickname").getText());
                        System.out.println("Marks : "+ student.getChild("marks").getText());	*/            		
                     }
            }
        }
        catch (JDOMException e) {
            throw new OAIException(14, e.getMessage());
        }
        return ret;
    }
	
}
