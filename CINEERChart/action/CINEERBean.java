package com.iddm.portlet.CINEERChart.action;
import java.util.*;
import com.iddm.util.IDDDecimalFormat;

public class CINEERBean{
		
	public List _col;
    public List _row;
    public boolean _decimal;
    public List _data;
    public IDDDecimalFormat _dfcomma;
    public String _chartDisplay;
	
	public void setColumn(List col){
		_col = col;
	}
	
	public List getColumn(){
		return _col;
	}
	
    public void setRow(List row){
        _row = row;
    }
    
    public List getRow(){
        return _row;
    }
    
    public void setData(List data){
        _data = data;
    }
    
    public List getData(){
        return _data;
    }    	
    
    public void setDecimal(boolean decimal){
        _decimal = decimal;
    }
    
    public boolean getDecimal(){
        return _decimal;
    }
    
    public void setDecimalFormat(IDDDecimalFormat dfcomma){
        _dfcomma = dfcomma;
    }
    
    public IDDDecimalFormat getDecimalFormat(){
        return _dfcomma;
    }

    public void setChartDisplay(String chartDisplay){
      _chartDisplay = chartDisplay;
    }

    public String getChartDisplay(){
      return _chartDisplay;
    }
}