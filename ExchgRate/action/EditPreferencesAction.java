/**
History of changes

DATE           By          Version   Remarks
-------------------------------------------------------------
Aaron     1.0.1     Create EditPreferencesAction for Summary of Rate Monitored
**/

package com.iddm.portlet.ExchgRate.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;
import com.iddm.util.Validator;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

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

        Validator validate = new Validator();
        String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
        //System.out.println("cmd-->"+cmd);

        if (!cmd.equals(Constants.UPDATE))
        {
            mapping.findForward("portlet.iddm.ExchgRate.view");
        }
        else
        {
            PortletPreferences prefs = req.getPreferences();
            String cmdUpdate = ParamUtil.getString(req,"update");
            //System.out.println("cmdUpdate->"+cmdUpdate);


        /***************Apply Security Checking******************/
        //System.out.println("Edit Preferences Security Check");
        String userId = PortalUtil.getUserId(req);
        //System.out.println("user id " + userId);
        String portletId = "17";
        String taskName = "Exchange Rate - Configure - Save";
        String portletName = "Exchange Rate";
        String backPath = "/html/common/frmError.jsp";

        try{
                PortletActionMaintenanceObject mObj = new PortletActionMaintenanceObject();

                //System.out.println("Check Object Init");
                if(mObj.objectInit(userId, portletName, backPath) == false) {
                    //System.out.println("Maintenance Object Init Failed");
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
                    return;
                }

                //System.out.println("Check Access Time");
                if (mObj.checkAccessTime() == false) {
                    //System.out.println("Access Time Invalid");
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmSystemFail.jsp");
                    return;
                }

                if (mObj.checkAuthorized(userId, portletId, portletName) == false){
                    //System.out.println("Unauthorized User ...." + userId);
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAccessDenied.jsp");
                    return;
                }

                if (mObj.checkTaskAssignment(taskName) == false) {
                    //System.out.println("No Task Assign to access the page " + userId + taskName);
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmTaskDenied.jsp");
                    return;
                }

                if (mObj.writeAuditTrail(taskName, userId) == false) {
                    //System.out.println("Insert Audit Trail Failed");
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuditTrailFail.jsp");
                    return;
                }

            }catch(Exception e){
                e.printStackTrace();
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
                return;
            }

            /***************End Security Checking******************/

            String errMsg = "";
            String thresCurr="";
            String thresRate="";
            String curr = "";
            if(cmdUpdate.equals("CURRENCY")){
                String[] col = StringUtil.split(
                    ParamUtil.getString(req, "table_currency"),
                    StringPool.UNDERLINE);
                for(int i=0; i <col.length; i++){
                    curr = (String)col[i].trim();
/*                    if (!Validator.isValidCurrency(curr,3)){
                       //System.out.println("Invalid currency (" + curr +")");
                       res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?portletName=" +portletName+"&taskName="+taskName+"&ex=Invalid currency ("+ curr +")");
                       return;
                    }
*/                }

                prefs.setValues("table_currency", col);

            }else if(cmdUpdate.equals("THRESHOLD")){
                String[] tempCol = StringUtil.split(
                    ParamUtil.getString(req, "thres_currency"),
                    StringPool.UNDERLINE);

                    for(int i=0; i <tempCol.length; i++){
                    thresCurr = (String)tempCol[i].trim();
/*                    if (!Validator.isValidCurrency(thresCurr,3)){
                       //System.out.println("Invalid currency (" + thresCurr +")");
                       res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?portletName=" +portletName+"&taskName="+taskName+"&ex=Invalid currency ("+ thresCurr +")");
                       return;
                        }
*/                    }

                    String[] tempRate = StringUtil.split(
                    ParamUtil.getString(req, "thres_rate"),
                    StringPool.UNDERLINE);

                    for(int i=0; i <tempRate.length; i++){
                    thresRate = tempRate[i].trim();
                    if (!Validator.DoubleOnly(thresRate)){
                       //System.out.println("Invalid Rate (" + thresRate +")");
                       res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?portletName=" +portletName+"&taskName="+taskName+"&ex=Invalid currency ("+ thresRate +")");
                       return;
                        }
                    }

                    prefs.setValues("thres_currency", tempCol);
                    prefs.setValues("thres_rate",tempRate);
            } else if(cmdUpdate.equals("RANGE")){
                String range = ParamUtil.getString(req, "table_range");
                if(!validate.isValidCharacter(range,"DWFMQY")) {
                    errMsg = "Invalid Range value ("+range+") <br> Valid value : D (Daily),W (Weekly),F (Fortnightly),M (Monthly),Q (Quarterly) and Y (Yearly)";
                    res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                    return;
                }
                prefs.setValue("table_range", range);

            }else if(cmdUpdate.equals("CURRENCY_SUMMARY")){
              String tmpCurr = ParamUtil.getString(req, "table_currency");
              //check whether the data is valid or not valid...
              if(!validate.isValidPreferences(tmpCurr)) {
                errMsg = "Invalid Currency format ("+tmpCurr+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              String[] currSum = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
              prefs.setValues("sum_currency", currSum);

            }else if(cmdUpdate.equals("TIME_SUMMARY")){
              String tmpTimeStart = ParamUtil.getString(req, "table_timestart");
              String tmpTimeEnd = ParamUtil.getString(req, "table_timeend");
              //check whether the data tmpTimeStart valid or not valid...
              if(!validate.isValidTime(tmpTimeStart)) {
                errMsg = "Invalid Time From format ("+tmpTimeStart+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              if(!validate.isValidTime(tmpTimeEnd)) {
                errMsg = "Invalid Time To format ("+tmpTimeEnd+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("sum_time_start", tmpTimeStart);
              prefs.setValue("sum_time_end", tmpTimeEnd);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_SUMMARY")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("sum_decimal", tmpDecimal);

            }else if(cmdUpdate.equalsIgnoreCase("RATETYPE_SUMMARY")){
              String[] tmpRateType = StringUtil.split(ParamUtil.getString(req, "table_ratetype"),StringPool.UNDERLINE);
              for(int i=0; i <tmpRateType.length; i++){
                if(!validate.isValidName(tmpRateType[i])) {
                  errMsg = "Invalid Rate Type Format ("+tmpRateType[i]+")";
                  res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                  return;
                }
              }
              prefs.setValues("sum_ratetype", tmpRateType);

            }else if(cmdUpdate.equals("CURRENCY_SUMMARYHIST")){
              String tmpCurr = ParamUtil.getString(req, "table_currency");
              //check whether the data is valid or not valid...
              if(!validate.isValidPreferences(tmpCurr)) {
                errMsg = "Invalid Currency format ("+tmpCurr+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              String[] currSum = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
              prefs.setValues("sum_currencyhist", currSum);

            }else if(cmdUpdate.equals("TIME_SUMMARYHIST")){
              String tmpTimeStart = ParamUtil.getString(req, "table_timestart");
              String tmpTimeEnd = ParamUtil.getString(req, "table_timeend");
              //check whether the data tmpTimeStart valid or not valid...
              if(!validate.isValidTime(tmpTimeStart)) {
                errMsg = "Invalid Time From format ("+tmpTimeStart+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              if(!validate.isValidTime(tmpTimeEnd)) {
                errMsg = "Invalid Time To format ("+tmpTimeEnd+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("sum_time_starthist", tmpTimeStart);
              prefs.setValue("sum_time_endhist", tmpTimeEnd);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_SUMMARYHIST")){
              String tmpDecimal= ParamUtil.getString(req, "table_decimal");
              if(!validate.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
                errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("sum_decimalhist", tmpDecimal);

            }else if(cmdUpdate.equalsIgnoreCase("RATETYPE_SUMMARYHIST")){
              String[] tmpRateType = StringUtil.split(ParamUtil.getString(req, "table_ratetype"),StringPool.UNDERLINE);
              for(int i=0; i <tmpRateType.length; i++){
                if(!validate.isValidName(tmpRateType[i])) {
                  errMsg = "Invalid Rate Type Format ("+tmpRateType[i]+")";
                  res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                  return;
                }
              }
              prefs.setValues("sum_ratetypehist", tmpRateType);

            }else if(cmdUpdate.equalsIgnoreCase("RATETYPE")){
              String tmpRateType= ParamUtil.getString(req, "table_ratetype");
              if(!validate.isValidName(tmpRateType)) {
                errMsg = "Invalid Rate Type Format ("+tmpRateType+")";
                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
                return;
              }
              prefs.setValue("ratetype", tmpRateType);

            }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_DAILY")){
        String tmpDecimal= ParamUtil.getString(req, "table_decimal");
        if(!Validator.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
          errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("decimaldaily", tmpDecimal);
      }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_WEEKLY")){
        String tmpDecimal= ParamUtil.getString(req, "table_decimal");
        if(!Validator.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
          errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("decimalweekly", tmpDecimal);
      }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_MTHLY")){
        String tmpDecimal= ParamUtil.getString(req, "table_decimal");
        if(!Validator.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
          errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("decimalmthly", tmpDecimal);
      }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_QTRLY")){
        String tmpDecimal= ParamUtil.getString(req, "table_decimal");
        if(!Validator.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
          errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("decimalqtrly", tmpDecimal);
      }else if(cmdUpdate.equalsIgnoreCase("DECIMAL_YRLY")){
        String tmpDecimal= ParamUtil.getString(req, "table_decimal");
        if(!Validator.isValidCharacter(tmpDecimal,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ")) {
          errMsg = "Invalid Decimal Format ("+tmpDecimal+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("decimalyrly", tmpDecimal);

      }else if(cmdUpdate.equals("CURRENCY_DAILY")){
        String tmpCurr    = ParamUtil.getString(req, "table_currency");
        //check whether the data is valid or not valid...
        if(!Validator.isValidPreferences(tmpCurr)) {
          errMsg = "Invalid preferences format ("+tmpCurr+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] curr_DAILY    = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
        prefs.setValues("currdaily", curr_DAILY);
      }else if(cmdUpdate.equals("CURRENCY_WEEKLY")){
        String tmpCurr    = ParamUtil.getString(req, "table_currency");
        //check whether the data is valid or not valid...
        if(!Validator.isValidPreferences(tmpCurr)) {
          errMsg = "Invalid preferences format ("+tmpCurr+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] curr_WEEKLY    = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
        prefs.setValues("currweekly", curr_WEEKLY);
      }else if(cmdUpdate.equals("CURRENCY_MTHLY")){
        String tmpCurr    = ParamUtil.getString(req, "table_currency");
        //check whether the data is valid or not valid...
        if(!Validator.isValidPreferences(tmpCurr)) {
          errMsg = "Invalid preferences format ("+tmpCurr+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] curr_MTHLY    = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
        prefs.setValues("currmthly", curr_MTHLY);
      }else if(cmdUpdate.equals("CURRENCY_QTRLY")){
        String tmpCurr    = ParamUtil.getString(req, "table_currency");
        //check whether the data is valid or not valid...
        if(!Validator.isValidPreferences(tmpCurr)) {
          errMsg = "Invalid preferences format ("+tmpCurr+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] curr_QTRLY    = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
        prefs.setValues("currqtrly", curr_QTRLY);
      }else if(cmdUpdate.equals("CURRENCY_YRLY")){
        String tmpCurr    = ParamUtil.getString(req, "table_currency");
        //check whether the data is valid or not valid...
        if(!Validator.isValidPreferences(tmpCurr)) {
          errMsg = "Invalid preferences format ("+tmpCurr+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] curr_YRLY    = StringUtil.split(tmpCurr,StringPool.UNDERLINE);
        prefs.setValues("curryrly", curr_YRLY);

      }

            try {
                prefs.store();
            }
            catch (ValidatorException ve) {
                SessionErrors.add(req, ValidatorException.class.getName(), ve);
                return;
            }

            SessionMessages.add(req, config.getPortletName() + ".doEdit");
        }

    }

    public ActionForward render(
            ActionMapping mapping, ActionForm form, PortletConfig config,
            RenderRequest req, RenderResponse res)
        throws Exception {

        return mapping.findForward("portlet.iddm.ExchgRate.view");
    }

}