/** History of changes
Date            By         Version  Remarks
-----------------------------------------------------------------------
19/10/2006      SoonPF      1.0.0    EditPreferencesActon
16/03/2007      WongPC      1.0.1    Add Rates Filter Criteria
***********************************************************************/

package com.iddm.portlet.CINEERChart.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;
import com.iddm.util.Validator;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.util.PortalUtil;
import com.iddm.util.PortletActionMaintenanceObject;

public class EditPreferencesAction extends PortletAction {

    public void processAction(
            ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest req, ActionResponse res)
        throws Exception {
        Validator validate=new Validator();
        String errMsg="";
        String prgmNm = "EditPreferencesAction";    
        
        String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
        System.out.println("cmd-->"+cmd);
        
        if (cmd.equals(Constants.UPDATE)){
            PortletPreferences prefs = req.getPreferences();
            String cmdUpdate = ParamUtil.getString(req,"update");
            System.out.println("cmdUpdate->"+cmdUpdate);

            /***************Apply Security Checking******************/
            System.out.println("Edit Preferences Security Check");  
            String userId = PortalUtil.getUserId(req);
            System.out.println("user id " + userId);            
            String portletId = "75";
            String taskName = "CI NEER - Configure - Save";
            String portletName = "CI NEER";  
            String backPath = "/html/common/frmError.jsp";              

            try{
                PortletActionMaintenanceObject mObj = new PortletActionMaintenanceObject();

                System.out.println("Check Object Init");
                if(mObj.objectInit(userId, portletName, backPath) == false) {
                    System.out.println("Maintenance Object Init Failed");     
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp"); 
                    return;
                }

                System.out.println("Check Access Time");
                if (mObj.checkAccessTime() == false) {
                    System.out.println("Access Time Invalid");  
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmSystemFail.jsp");                                   
                    return;
                }

                if (mObj.checkAuthorized(userId, portletId, portletName) == false){
                    System.out.println("Unauthorized User ...." + userId);                                  
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAccessDenied.jsp");                                 
                    return;
                }

                if (mObj.checkTaskAssignment(taskName) == false) {
                    System.out.println("No Task Assign to access the page " + userId + taskName);
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmTaskDenied.jsp");                                   
                    return;
                }

                if (mObj.writeAuditTrail(taskName, userId) == false) {
                    System.out.println("Insert Audit Trail Failed");                    
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuditTrailFail.jsp");                                   
                    return;
                }

            }catch(Exception e){
                e.printStackTrace();
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
                return;
            }

            /***************End Security Checking******************/

            if(cmdUpdate.equals("CURRENCY_CHART")){
                String tmpCol = ParamUtil.getString(req, "chart_currency");
                
                //check whether the data is valid or not valid...
                if(!validate.isValidCharacter(tmpCol,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz,'")) {
                    errMsg = "Invalid currency format ("+tmpCol+")";                    
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                    return;
                }
                prefs.setValue("chart_currency", tmpCol);                     
            }else if(cmdUpdate.equalsIgnoreCase("DECIMAL")){
                String tmpDecimal= ParamUtil.getString(req, "table_decimal");
                if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                  errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                  res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                  return;
                }
                prefs.setValue("decimal", tmpDecimal);
        
            }else if(cmdUpdate.equalsIgnoreCase("DATERANGE")){
                String tmpRange= ParamUtil.getString(req, "range");
                if(!validate.isValidCharacter(tmpRange,"0123456789|")) {
                  errMsg = "Invalid Date Range Format ("+tmpRange+")";
                  res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                  return;
                }
                prefs.setValue("date_range", tmpRange);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMALHIST")){
                String tmpDecimal= ParamUtil.getString(req, "table_decimal");
                if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                  errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                  res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                  return;
                }
                prefs.setValue("decimalhist", tmpDecimal);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMALWEEK")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("decimalweek", tmpDecimal);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMALMTH")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("decimalmth", tmpDecimal);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMALQTR")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("decimalqtr", tmpDecimal);

           }else if(cmdUpdate.equalsIgnoreCase("DECIMALYEAR")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("decimalyear", tmpDecimal);

           }else if(cmdUpdate.equalsIgnoreCase("RATESFILTER")){
              String ratesfilter= ParamUtil.getString(req, "table_ratesfilter");
              if(!validate.isValidCharacter(ratesfilter,"YN")) {
                errMsg = "Invalid Integer Value ("+ratesfilter+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("sRatesFilter", ratesfilter);

            }

            try {
                prefs.store();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                return;
            }
        }
        
    }

    public ActionForward render(
            ActionMapping mapping, ActionForm form, PortletConfig config,
            RenderRequest req, RenderResponse res)
        throws Exception {

        return mapping.findForward("portlet.iddm.view");
    }
}