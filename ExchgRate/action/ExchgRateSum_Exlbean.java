
/***************************Start History of Changes**************************
Date        Name         Version      Remarks
------------------------------------------------------------------------------
Aaron      1.0.0        - Exchange Rate (Summary of Rate Monitored) Exlbean
****************************End History of Changes***************************/

package com.iddm.portlet.ExchgRate.action;
import java.util.*;

public class ExchgRateSum_Exlbean{

  public String _year1; 
  public String _month1;
  public String _day1;
  public String _year2; 
  public String _month2;
  public String _day2;
  public List _ColumnDataLeaf;
  public List _Column;
  public List _Row;
  public List _Data;
  public String _Title;

  public void setYear1(String sYear1){
    _year1 = sYear1;
  }

  public String getYear1(){
    return _year1;
  }

  public void setMonth1(String sMonth1){
    _month1 = sMonth1;
  }

  public String getMonth1(){
    return _month1;
  }

  public void setDay1(String sDay1){
    _day1 = sDay1;
  }

  public String getDay1(){
    return _day1;
  }
  public void setYear2(String sYear2){
    _year2 = sYear2;
  }

  public String getYear2(){
    return _year2;
  }

  public void setMonth2(String sMonth2){
    _month2 = sMonth2;
  }

  public String getMonth2(){
    return _month2;
  }

  public void setDay2(String sDay2){
    _day2 = sDay2;
  }

  public String getDay2(){
    return _day2;
  }
  
  public void setColumnDataLeaf(List sColumnDataLeaf){
    _ColumnDataLeaf = sColumnDataLeaf;
  }

  public List getColumnDataLeaf(){
    return _ColumnDataLeaf;
  }

  public void setColumn(List sColumn){
    _Column = sColumn;
  }

  public List getColumn(){
    return _Column;
  }

  public void setRow(List sRow){
    _Row = sRow;
  }

  public List getRow(){
    return _Row;
  }

  public void setData(List sData){
    _Data = sData;
  }

  public List getData(){
    return _Data;
  }

  public void setTitle(String sTitle){
    _Title = sTitle;
  }

  public String getTitle(){
    return _Title;
  }
}