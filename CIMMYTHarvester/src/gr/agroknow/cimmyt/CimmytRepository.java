package gr.agroknow.cimmyt;

import uiuc.oai.OAIException;
import uiuc.oai.OAIRecordList;
import uiuc.oai.OAIRepository;
import uiuc.oai.OAIResumptionStream;

public class CimmytRepository extends OAIRepository {

	public CimmytRecordList listRecords() throws OAIException {
        return this.listRecords("oai_dc", "", "", "");
    }

    public CimmytRecordList listRecords(String metadataPrefix) throws OAIException {
        return this.listRecords(metadataPrefix, "", "", "");
    }

    public CimmytRecordList listRecords(String metadataPrefix, String untild) throws OAIException {
        return this.listRecords(metadataPrefix, untild, "", "");
    }

    public CimmytRecordList listRecords(String metadataPrefix, String untild, String fromd) throws OAIException {
        return this.listRecords(metadataPrefix, untild, fromd, "");
    }

    public CimmytRecordList listRecords(String metadataPrefix, String untild, String fromd, String setSpec) throws OAIException {
        this.priCheckBaseURL();
        String prefix = metadataPrefix;
        prefix = metadataPrefix.length() == 0 ? "oai_dc" : metadataPrefix;
        String params = this.priBuildParamString(untild, fromd, setSpec, "", prefix);
        OAIResumptionStream rs = new OAIResumptionStream(this, this.strBaseURL, "ListRecords", params);
        CimmytRecordList sets = new CimmytRecordList();
        sets.frndSetMetadataPrefix(metadataPrefix);
        sets.frndSetOAIResumptionStream(rs);
        return sets;
    }

    public CimmytRecordList listAllRecords(String metadataPrefix, String setSpec) throws OAIException {
        this.priCheckBaseURL();
        String prefix = metadataPrefix;
        prefix = metadataPrefix.length() == 0 ? "oai_dc" : metadataPrefix;
        String params = this.priBuildParamString(null, null, setSpec, "", prefix);
        OAIResumptionStream rs = new OAIResumptionStream(this, this.strBaseURL, "ListRecords", params);
        CimmytRecordList sets = new CimmytRecordList();
        sets.frndSetMetadataPrefix(metadataPrefix);
        sets.frndSetOAIResumptionStream(rs);
        return sets;
    }

	
}
