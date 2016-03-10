package gr.agroknow.cimmyt;

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
            Vector<Namespace> nsVector = new Vector<Namespace>();
            nsVector.add(oains);
            Element node = OaiUtil.getXpathNode("oai:metadata/*", nsVector, this.xmlRecord);
            if (node != null) {
                node = (Element)node.clone();
                node.detach();
                ret = new Document(node).getRootElement();
                
                System.out.println(ret.getAttributeValue("dc:description"));
            }
        }
        catch (JDOMException e) {
            throw new OAIException(14, e.getMessage());
        }
        return ret;
    }
	
}
