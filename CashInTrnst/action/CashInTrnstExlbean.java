/***************************Start History of Changes**************************
Date        Name         Version      Remarks
------------------------------------------------------------------------------
  Aaron       1.0.0        -OutSwapBank bean
****************************End History of Changes***************************/

package com.iddm.portlet.CashInTrnst.action;
import java.util.*;

public class CashInTrnstExlbean{

	public String sTblDate;
    public String sTblCur;  
    public String sTblAmt;
    public String sTblCust;
    
    
	public void setSTblDate(String date){
        sTblDate = date;
    }
    
    public String getSTblDate(){
        return sTblDate;
    }
    
    public void setSTblCur(String cur){
        sTblCur = cur;
    }
    
    public String getSTblCur(){
        return sTblCur;
    }
            	
    public void setSTblAmt(String amt){
        sTblAmt = amt;
    }
    
    public String getSTblAmt(){
        return sTblAmt;
    }

    public void setSTblCust(String cust){
        sTblCust = cust;
    }
    
    public String getSTblCust(){
        return sTblCust;
    }    
}