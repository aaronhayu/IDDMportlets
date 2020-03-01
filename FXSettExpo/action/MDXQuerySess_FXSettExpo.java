package com.iddm.portlet.FXSettExpo.action;
import java.util.*;

public class MDXQuerySess_FXSettExpo{

  public String MDXQuery;
  public String LayoutId;
  public String userId;
  public String portletId;
  public String dateType; 
  public String selectedPage;

  public void setMDXQuery(String Query){
    MDXQuery = Query;
  }

  public String getMDXQuery(){
    return MDXQuery;
  }

  public void setLayoutId(String lId){
    LayoutId = lId;
  }
  
  public String getLayoutId(){
    return LayoutId;
  }

  public void setuserId(String uId){
    userId = uId;
  }

  public String getuserId(){
    return userId;
  }

  public void setportletId(String pId){
    portletId = pId;
  }

  public String getportletId(){
    return portletId;
  }

  public void setDateType(String strDateType){
    dateType= strDateType;
  }

  public String getDateType(){
    return dateType;
  }

  public void setSelectedPage(String strSelectedPage){
    selectedPage= strSelectedPage;
  }

  public String getSelectedPage(){
    return selectedPage;
  }
}