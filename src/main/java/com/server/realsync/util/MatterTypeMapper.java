/**
 * 
 */
package com.server.realsync.util;

/**
 * 
 */
import java.util.Map;
import java.util.HashMap;

public class MatterTypeMapper {

    private static final Map<String, Integer> valueToIdMap = new HashMap<>();

    static {
        valueToIdMap.put("TCA(MD)_TAX CASES APPEAL(MD)", 253);
        valueToIdMap.put("CMA NPDB(MD)_CI MISC.APPL NPD(MD)", 210);
        valueToIdMap.put("WP Crl._Writ Petition Criminal", 334);
        valueToIdMap.put("CMSA(MD)_CI.MISC. II AP. (MD)", 215);
        valueToIdMap.put("APPEAL(CAD)(MD)_Appeal - Commercial Appellate Division (MD)", 275);
        valueToIdMap.put("REV.APLWP Crl._REV. APPLICATION WRIT PETITION CRIMINAL", 349);
        valueToIdMap.put("WPMP Crl._Writ Miscellaneous Petition Criminal", 346);
        valueToIdMap.put("CONT AL(MD)_CONTEMPT APPEAL(MD) ", 267);
        valueToIdMap.put("STA(MD)_SPL.TRIBUNAL APP(MD)", 250);
        valueToIdMap.put("TCR_TAX CASES REV.", 270);
        valueToIdMap.put("WP Crl.(MD)_Writ Petition Criminal (MD)", 276);
        valueToIdMap.put("CS(MD)_CIVIL SUITS(MD)", 225);
        valueToIdMap.put("APPLN(MD)_APPLICATION(OS2)(MD)", 203);
        valueToIdMap.put("RCMP(MD)_RCP MIS.PETITION(MD)", 240);
        valueToIdMap.put("Arb O.P_Arbitration Original Petition", 286);
        valueToIdMap.put("CONT A(MD)_CONTEMPT APPEAL(MD) ", 217);
        valueToIdMap.put("WPMP Crl.(MD)_Writ Miscellaneous Petition Criminal (MD)", 347);
        valueToIdMap.put("REV.APLWP Crl.(MD)_REV. APPLICATION WRIT PETITION CRIMINAL (MD)", 348);
        valueToIdMap.put("OA(MD)_ORIGINAL APPLN.(MD) ", 235);
        valueToIdMap.put("COMP.A(MD)_CO. APPLICATION (MD)", 216);
        valueToIdMap.put("AS B(MD)_I APPEAL BENCH(MD)", 205);
        valueToIdMap.put("CROS.OBJ_CROSS OBJECTION", 136);
        valueToIdMap.put("WVMP(MD)_VACATING ORD.MP(MD) ", 266);
        valueToIdMap.put("CMP_CMP", 113);
        valueToIdMap.put("AS_Appeal Suit", 1);
        valueToIdMap.put("CMA DB(MD)_CMA BENCH(MD)", 208);
        valueToIdMap.put("WP_WRIT PETITION", 49);
        valueToIdMap.put("HCP_HABEAS CORPUS PETITION", 22);
        valueToIdMap.put("APPEAL(CAD)_Appeal - Commercial Appellate Division", 283);
        valueToIdMap.put("RCP(MD)_REFERD. CASE PET(MD)", 241);
        valueToIdMap.put("TOS(MD)_TESTAMENTARY OS (MD)", 258);
        valueToIdMap.put("TCMP(MD)_TAX CMP(MD)", 254);
        valueToIdMap.put("REV.APPL_REVIEW APPLICATION", 35);
        valueToIdMap.put("CMA_CIVIL MISCELLANEOUS APPEAL", 2);
        valueToIdMap.put("CMA NPDS(MD)_CI.MIS.APPL NPDS(MD)", 211);
        valueToIdMap.put("CMA PDB(MD)_CI.MIS. APPL.PDB(MD)", 213);
        valueToIdMap.put("CMA PD(MD)_CMA Pending(MD)", 212);
        valueToIdMap.put("CMA PDS(MD)_CI.MIS.APPL. PDS(MD)", 214);
        valueToIdMap.put("CMSA_CMSA", 5);
        valueToIdMap.put("OSA(MD)_ORI.SIDE APPEAL (MD)", 238);
        valueToIdMap.put("HCMP(MD)_HABEAS CORPUS MP(MD)", 228);
        valueToIdMap.put("CONT A_CONT A", 7);
        valueToIdMap.put("CONT APP(MD)_CONTEMPT APPLN. (MD)", 218);
        valueToIdMap.put("CRP_CIVIL REVISION PETITION", 12);
        valueToIdMap.put("TR AS(MD)_TRANSFER I APPL.(MD)", 260);
        valueToIdMap.put("CRP PD_CRP PD", 17);
        valueToIdMap.put("CRP NPD_CRP NPD", 16);
        valueToIdMap.put("VCMP(MD)_VACATING ORD. MP(MD)", 262);
        valueToIdMap.put("WPMP(MD)_W.P. MISC. PET.(MD)", 265);
        valueToIdMap.put("SCMP(MD)_SUP.COURT MISC.P(MD)", 248);
        valueToIdMap.put("LPA(MD)_LTRS.PATENT APPL(MD)", 233);
        valueToIdMap.put("REV.APLC_REVIEW APPLICATION CIVIL", 32);
        valueToIdMap.put("TMA(MD)_TRADE MARKS APL.(MD)", 256);
        valueToIdMap.put("CMA NPD(MD)_CMA Non Pending(MD)", 209);
        valueToIdMap.put("CRL OP_CRIMINAL ORIGINAL PETITION", 52);
        valueToIdMap.put("CRP NPD(MD)_CI.REV. PET.NPD (MD)", 223);
        valueToIdMap.put("A(MD)_APPLICATION(MD)", 201);
        valueToIdMap.put("LPA_LPA", 24);
        valueToIdMap.put("IP(MD)_INSOLVENCY PET.(MD)", 232);
        valueToIdMap.put("IC(MD)_INSOLVENCY CASES(MD)", 231);
        valueToIdMap.put("TR APPL(MD)_TRANSFER APPLN.(MD)", 259);
        valueToIdMap.put("WAMP(MD)_W.A. MISC. PET.(MD)", 263);
        valueToIdMap.put("OP(MD)_ORIGINAL PETITN.(MD)", 237);
        valueToIdMap.put("TR CMA(MD)_TRANSFER CI.MISC(MD)", 242);
        valueToIdMap.put("REV.STP(MD)_REV.SPL.TRIBUNAL(MD)", 245);
        valueToIdMap.put("O.P(COMM DIV)_Original Petition (Commercial Division)", 282);
        valueToIdMap.put("CRL RC_CRIMINAL REVISION CASE", 13);
        valueToIdMap.put("CRL A_Criminal Appeal", 273);
        valueToIdMap.put("RC(MD)_REFERRED CASES(MD)", 239);
        valueToIdMap.put("TCR(MD)_TAX CASES REV.(MD)", 268);
        valueToIdMap.put("Arb Appeal(MD)_Arbitration Appeal(MD)", 345);
        valueToIdMap.put("TCTOP(MD)_Transferred City Tenant Original Petition(MD)", 343);
        valueToIdMap.put("STP(MD)_SPL.TRIBUNAL PET(MD)", 251);
        valueToIdMap.put("MC(MD)_MATRIMO. CAUSES(MD)", 234);
        valueToIdMap.put("TC(MD)_TAX CASES", 271);
        valueToIdMap.put("TCP(MD)_TAX CASE PET(MD)", 255);
        valueToIdMap.put("APPL.(MD)_APPLICATION(MD)", 202);
        valueToIdMap.put("RT(MD)_REFERRED TRIAL(MD)", 246);
        valueToIdMap.put("TMSA(MD)_TRADE MARKS SA (MD)", 257);
        valueToIdMap.put("TR OS(MD)_Transferred Original Suit(MD)", 344);
        valueToIdMap.put("ELP_ELECTION PETITION", 144);
        valueToIdMap.put("HCP(MD)_HABEAS CORPUS PETITION(MD)", 229);
        valueToIdMap.put("CRL REF(MD)_CRIMINAL REFERENCE", 168);
        valueToIdMap.put("ELP(MD)_ELECT. PETITIONS(MD)", 226);
        valueToIdMap.put("REV.APPL(MD)_REVIEW APPLNS.(MD)", 244);
        valueToIdMap.put("CRL MP_CRIMINIAL MISC.PETITION", 274);
        valueToIdMap.put("AS S(MD)_I APPEAL SINGLE(MD)", 206);
        valueToIdMap.put("SA_SECOND APPEAL", 38);
        valueToIdMap.put("REV.APCR(MD)_REVIEW APPLNS.(MD)", 269);
        valueToIdMap.put("CRP PD(MD)_CI. REV. PET. PD(MD)", 224);
        valueToIdMap.put("IA(MD)_INSOLVENCY APPLN(MD)", 230);
        valueToIdMap.put("STA_STA", 39);
        valueToIdMap.put("EP(MD)_EXECUT. PETITION(MD)", 227);
        valueToIdMap.put("OMS(MD)_ORI.MATRI.SUITS(MD)", 236);
        valueToIdMap.put("OSA(CAD)_Original Side Appeal-Commercial Appellate Division", 284);
        valueToIdMap.put("COM APEL(MD)_COMPANY APPEAL(MD)", 272);
        valueToIdMap.put("WMP_WRIT MISC PETITION", 133);
        valueToIdMap.put("Arb Appln_Arbitration Application", 285);
        valueToIdMap.put("CP(MD)_COMPANY PETITION(MD)", 219);
        valueToIdMap.put("WA_WRIT APPEAL", 48);
        valueToIdMap.put("SCP(MD)_SUP.COURT PETI.(MD)", 249);
        valueToIdMap.put("C.S(COMM DIV)_Civil Suit (Commercial Division)", 281);
        valueToIdMap.put("TRCS(MD)_TRANSFER CI.SUIT(MD)", 261);
        valueToIdMap.put("TR AS_TRANSFER  APPL", 128);
        valueToIdMap.put("WMP(MD)_WRIT MISC. PET.(MD)", 264);
        valueToIdMap.put("REV.APLC(MD)_REVIEW APPLICATION CIVIL(MD)", 167);
        valueToIdMap.put("WA(MD)_WRIT APPEAL(MD)", 156);
        valueToIdMap.put("AS(MD)_FIRST APPEAL(MD)", 204);
        valueToIdMap.put("CMP(MD)_CIVIL MISC. PETITION (MD)", 159);
        valueToIdMap.put("CRP(MD)_CIVIL REVISION PETITION(MD)", 158);
        valueToIdMap.put("WP(MD)_Writ Petition(MD)", 155);
        valueToIdMap.put("SUB A(MD)_SUB APPLICATION(MD)", 252);
        valueToIdMap.put("CROS.OBJ(MD)_CROSS OBJECTION(MD)", 222);
        valueToIdMap.put("CRL A(MD)_CRIMINAL APPEAL(MD)", 220);
        valueToIdMap.put("CRL MP(MD)_CRIMINIAL MISC.PETITION(MD)", 163);
        valueToIdMap.put("CRL OP(MD)_CRIMINAL ORIGINAL PETITION(MD)", 162);
        valueToIdMap.put("REV.APLW(MD)_REV. APPLICATION WRIT (MD)", 160);
        valueToIdMap.put("TR CMP(MD)_TRANSFER CI.MISC(MD)", 243);
        valueToIdMap.put("CMA(MD)_CI. MISC. APPEAL(MD)", 207);
        valueToIdMap.put("CONT P(MD)_CONTEMPT PETITION (MD)", 166);
        valueToIdMap.put("SA(MD)_SECOND APPEAL(MD)", 247);
        valueToIdMap.put("CRL RC(MD)_CRIMINAL REVISION CASE(MD)", 221);
    }

    /**
     * Returns the id for the given value, or null if not found.
     */
    public static Integer getIdByValue(String value) {
        return valueToIdMap.get(value);
    }
    
    public static Integer getIdByPartialValue(String input) {
    	try {
    		for (Map.Entry<String, Integer> entry : valueToIdMap.entrySet()) {
                if (entry.getKey().startsWith(input)) { // or contains(input) if you want anywhere match
                    return entry.getValue();
                }
            }
    	} catch(Exception e) {
    		System.out.println("Exception in parsing "+input);
    	}
        
        return null; // not found
    }


    // Example usage:
    public static void main(String[] args) {
        String input = "SA(MD)";
        Integer id = getIdByPartialValue(input);
        System.out.println("ID = " + id); // Output: 247
    }
}
