
/***************************Start History of Changes**************************
Date        Name         Version      Remarks
------------------------------------------------------------------------------
Aaron       1.0.0        - Exchange Rate (Summary of Rate Monitored) Exlbean
****************************End History of Changes***************************/

package com.iddm.portlet.ExchgRate.action;
import java.util.*;

public class ExchgRateSum_Exlbean{

  public String _year; 
  public String _month;
  public String _day;
  public List _ColumnDataLeaf;
  public List _Column;
  public List _Row;
  public List _Data;
  public String _Title;

  public void setYear(String sYear){
    _year = sYear;
  }

  public String getYear(){
    return _year;
  }

  public void setMonth(String sMonth){
    _month = sMonth;
  }

  public String getMonth(){
    return _month;
  }

  public void setDay(String sDay){
    _day = sDay;
  }

  public String getDay(){
    return _day;
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