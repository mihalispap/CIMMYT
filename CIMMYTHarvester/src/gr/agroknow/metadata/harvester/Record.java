package gr.agroknow.metadata.harvester;

import org.jdom.Document;
import org.jdom.Element;

import gr.agroknow.cimmyt.CimmytRecord;
import uiuc.oai.OAIException;
import uiuc.oai.OAIRecord;

public class Record {

	protected Element metadata;
	protected String oaiIdentifier;
	protected CimmytRecord oaiRecord;
	
	public Element getMetadata() {
		return metadata;
	}
	public void setMetadata(Element metadata) {
		this.metadata = metadata;
	}
	public String getOaiIdentifier() {
		return oaiIdentifier;
	}
	public void setOaiIdentifier(String oaiIdentifier) {
		if(oaiIdentifier != null)this.oaiIdentifier = oaiIdentifier.trim();
	}
	public OAIRecord getOaiRecord() {
		return oaiRecord;
	}
	public void setOaiRecord(CimmytRecord oaiRecord) throws OAIException {
			this.oaiRecord = oaiRecord;
			setMetadata(oaiRecord.getMetadata());
			setOaiIdentifier(oaiRecord.getIdentifier());
	}
	
	
}
