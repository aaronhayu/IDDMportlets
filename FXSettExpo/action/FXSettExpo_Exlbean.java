
/***************************Start History of Changes**************************
Date        Name         Version      Remarks
------------------------------------------------------------------------------
Aaron      1.0.0        - Create FXSettExpo_Exlbean.java
****************************End History of Changes***************************/

package com.iddm.portlet.FXSettExpo.action;
import java.util.*;

public class FXSettExpo_Exlbean{
  public String _sDate;
  public String _sAmount;
  public String _sBranch;
  public List _sData;
  public List _sCounterparty;
  public String[] _sDesc;
  public String[] _sRemark;
  public String[] _sTitle;
  public String[] _sTitle2;
  public String[] _sTitleLen;
  public String _sTotalCol;
  public String _sDataItemSize;
  public List _sRowBg;
  public String _sColumnSpan;

  public void setDate(String sDate){
    _sDate = sDate;
  }

  public String getDate(){
    return _sDate;
  }

  public void setAmount(String sAmount){
    _sAmount = sAmount;
  }

  public String getAmount(){
    return _sAmount;
  }

  public void setBranch(String sBranch){
    _sBranch = sBranch;
  }

  public String getBranch(){
    return _sBranch;
  }

  public void setData(List sData){
    _sData = sData;
  }

  public List getData(){
    return _sData;
  }

  public void setCounterparty(List sCounterparty){
    _sCounterparty = sCounterparty;
  }

  public List getCounterparty(){
    return _sCounterparty;
  }

  public void setDesc(String[] sDesc){
    _sDesc = sDesc;
  }

  public String[] getDesc(){
    return _sDesc;
  }

  public void setRemark(String[] sRemark){
    _sRemark = sRemark;
  }

  public String[] getRemark(){
    return _sRemark;
  }

  public void setTitle(String[] sTitle){
    _sTitle = sTitle;
  }

  public String[] getTitle(){
    return _sTitle;
  }

  public void setTitle2(String[] sTitle2){
    _sTitle2 = sTitle2;
  }

  public String[] getTitle2(){
    return _sTitle2;
  }

  public void setTitleLen(String[] sTitleLen){
    _sTitleLen = sTitleLen;
  }

  public String[] getTitleLen(){
    return _sTitleLen;
  }
  
  public void setTotalCol(String sTotalCol){
    _sTotalCol = sTotalCol;
  }

  public String getTotalCol(){
    return _sTotalCol;
  }
 
 public void setDataItemSize(String DataItemSize){
    _sDataItemSize = DataItemSize;
  }

  public String getDataItemSize(){
    return _sDataItemSize;
  }
  
   
  public void setRowBg(List sRowBg)
  {
    _sRowBg = sRowBg;
  }

  public List getRowBg()
  {
    return _sRowBg;
  }
    
  public void setColumnSpan(String sColumnSpan){
    _sColumnSpan = sColumnSpan;
  }

  public String getColumnSpan(){
    return _sColumnSpan;
  }
}