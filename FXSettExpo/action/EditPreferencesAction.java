/**
History of changes

DATE           By          Remarks
-------------------------------------------------------------
Aaron     Create EditPreferencesAction
**/

package com.iddm.portlet.FXSettExpo.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;

import java.util.Arrays;

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

import com.iddm.util.Validator;
import com.iddm.util.IDDMdxQueryHib;
import email.TRMSEmailSender;

import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import com.liferay.portlet.PortletURLImpl;
import com.liferay.portlet.ActionRequestImpl;
import javax.portlet.PortletMode;
import com.liferay.portal.util.WebKeys;
import com.liferay.portal.model.Layout;
import java.util.List;
import java.util.ArrayList;

public class EditPreferencesAction extends PortletAction {
  public void processAction(
    ActionMapping mapping, ActionForm form, PortletConfig config,
    ActionRequest req, ActionResponse res)
    throws Exception {

    String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
    if (cmd.equals(Constants.UPDATE)){
      PortletPreferences prefs = req.getPreferences();
      String cmdUpdate = ParamUtil.getString(req,"update");

      /***************Apply Security Checking******************/
      String userId = PortalUtil.getUserId(req);
      //String portletId = "82";
      String portletId = "98";
      //String taskName = "Foreign Deposit Limit Utilisation - Configure - Save";
      String taskName = "FX Settlement - Configure - Save";      
      //String portletName = "Foreign Deposit Limit Utilisation";
      String portletName = "FX Settlement";
      String backPath = "/html/common/frmError.jsp";

      try{
        PortletActionMaintenanceObject mObj = new PortletActionMaintenanceObject();
        if(mObj.objectInit(userId, portletName, backPath) == false) {
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
          return;
        }
        if (mObj.checkAccessTime() == false) {
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmSystemFail.jsp");
          return;
        }
        if (mObj.checkAuthorized(userId, portletId, portletName) == false){
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmAccessDenied.jsp");
          return;
        }
        if (mObj.checkTaskAssignment(taskName) == false) {
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmTaskDenied.jsp");
          return;
        }
        if (mObj.writeAuditTrail(taskName, userId) == false) {
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuditTrailFail.jsp");
          return;
        }
      }catch(Exception e){
        e.printStackTrace();
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
        return;
      }
      /***************End Security Checking******************/

      Validator validate = new Validator();
      String errMsg = "";
      if(cmdUpdate.equals("AMOUNT")){
        String tmpAmount = ParamUtil.getString(req, "table_amount");
        if(!validate.isValidPreferences(tmpAmount)) {
          errMsg = "Invalid preferences format ("+tmpAmount+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("table_amount", tmpAmount);

      }else if(cmdUpdate.equals("FXSettAMOUNT")){
        String tmpAmount = ParamUtil.getString(req, "table_amount");
        if(!validate.isValidPreferences(tmpAmount)) {
          errMsg = "Invalid preferences format ("+tmpAmount+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("FXSetttable_amount", tmpAmount);

      }else if(cmdUpdate.equals("EWACS")){
        String tmpEwacs = ParamUtil.getString(req, "table_ewacs");        
        
        prefs.setValue("EWACS", tmpEwacs);

      }else if(cmdUpdate.equals("COUNTERPARTY")){
        String tmpCounterparty    = ParamUtil.getString(req, "table_counterparty");
        if(!validate.isValidPreferences(tmpCounterparty)) {
          errMsg = "Invalid preferences format ("+tmpCounterparty+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] counterparty = StringUtil.split(tmpCounterparty,StringPool.UNDERLINE);
        prefs.setValues("table_counterparty", counterparty);

      }else if(cmdUpdate.equals("FXSettCOUNTERPARTY")){
        String tmpCounterparty    = ParamUtil.getString(req, "table_counterparty");
        if(!validate.isValidPreferences(tmpCounterparty)) {
          errMsg = "Invalid preferences format ("+tmpCounterparty+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        String[] counterparty = StringUtil.split(tmpCounterparty,StringPool.UNDERLINE);
        prefs.setValues("FXSetttable_counterparty", counterparty);

      }else if(cmdUpdate.equalsIgnoreCase("BRANCH")){
        String tmpBranch = ParamUtil.getString(req, "table_branch");
        if(!validate.isValidPreferences(tmpBranch)) {
          errMsg = "Invalid preferences format ("+tmpBranch+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("table_branch", tmpBranch);

      }else if(cmdUpdate.equalsIgnoreCase("FXSettBRANCH")){
        String tmpBranch = ParamUtil.getString(req, "FXSetttable_branch");
        if(!validate.isValidPreferences(tmpBranch)) {
          errMsg = "Invalid preferences format ("+tmpBranch+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("FXSetttable_branch", tmpBranch);

      }else if(cmdUpdate.equals("AMOUNT_JPIVOT")){
        String tmpAmount = ParamUtil.getString(req, "table_amount");
        if(!validate.isValidPreferences(tmpAmount)) {
          errMsg = "Invalid preferences format ("+tmpAmount+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("jpivot_amount", tmpAmount);

      }else if(cmdUpdate.equalsIgnoreCase("BRANCH_JPIVOT")){
        String tmpBranch = ParamUtil.getString(req, "table_branch");
        if(!validate.isValidPreferences(tmpBranch)) {
          errMsg = "Invalid preferences format ("+tmpBranch+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("jpivot_branch", tmpBranch);

      }else if(cmdUpdate.equals("PAGE_JPIVOT")){
        String defaultpage = ParamUtil.getString(req, "defaultpage");
        prefs.setValue("DefaultPage",defaultpage);

      }else if(cmdUpdate.equals("DELETEPAGE_JPIVOT")){
        String seqNo = ParamUtil.getString(req,"PageName");
        String sLayoutId = ParamUtil.getString(req,"LayoutId");
        try{
          IDDMdxQueryHib MdxQryH = new IDDMdxQueryHib();
          MdxQryH.DOIE_DeletePage(seqNo,portletId,userId,sLayoutId,"Foreign Deposit Limit Utilisation - EWACS");
          prefs.setValue("DefaultPage","System Default Page");
        }catch(Exception ex){
          String sErrMsg = ex.getMessage();
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmErrMsg_sec.jsp?sErrMsg =" + sErrMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
      }else if(cmdUpdate.equals("COUNTERPARTY_CHART")){
        String tmpCounterparty    = ParamUtil.getString(req, "table_counterparty");
        if(!validate.isValidPreferences(tmpCounterparty)) {
          errMsg = "Invalid preferences format ("+tmpCounterparty+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        //---Replace '++' to '%'
        tmpCounterparty = validate.ReplaceChar(tmpCounterparty,"++","%");
        String[] counterparty = StringUtil.split(tmpCounterparty,StringPool.UNDERLINE);
        prefs.setValues("chart_counterparty", counterparty);

      }else if(cmdUpdate.equalsIgnoreCase("BRANCH_CHART")){
        String tmpBranch = ParamUtil.getString(req, "table_branch");
        if(!validate.isValidPreferences(tmpBranch)) {
          errMsg = "Invalid preferences format ("+tmpBranch+")";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        prefs.setValue("chart_branch", tmpBranch);
       
      }/*send email*/
     else if(cmdUpdate.equals("EMAIL")){
        //Parameters
        String Yr = req.getParameter("Yr");
        String Mth = req.getParameter("Mth");
        String Dy = req.getParameter("Dy");
        String emailClass = req.getParameter("emailClass");
        String cat = req.getParameter("cat");
        String reportName = req.getParameter("reportName");
        String description = req.getParameter("description");
        String summary = req.getParameter("summary");
        String details = req.getParameter("details");
        String title = req.getParameter("title");
        String date = req.getParameter("date");
        String dirpath = req.getParameter("dirpath");
        String ewacsCode = req.getParameter("ewacsCode");
        String subject = req.getParameter("subject");
        String[] arrEmailClass = StringUtil.split(emailClass,StringPool.LINE);
        
        String[] arrSummary2 = StringUtil.split(summary,StringPool.UNDERLINE);
        String[] arrDetails2 = StringUtil.split(details,StringPool.UNDERLINE);
        String[] arrTitle = StringUtil.split(title,StringPool.LINE);
        String[] arrEwacsCode = StringUtil.split(ewacsCode,StringPool.LINE);

        //---Pass Param TRMSEmailSender  
        boolean exist = false;      
       for (int i=0; i<arrEmailClass.length; i++){
       	  exist = false;
       	  List lstDetails = new ArrayList();
       	  //chk whether there is a need to send eml onot
       	  for (int j=0; j<arrDetails2.length; j++){
       	  	String[] arrtmpDetails = StringUtil.split(arrDetails2[j],StringPool.LINE);
       	  	for (int k=0; k<arrtmpDetails.length; k++){
              if (k==arrtmpDetails.length-3){
                String Tdy = arrtmpDetails[k];
                if (Tdy.equals(arrEwacsCode[i]))
                  lstDetails.add(arrDetails2[j]);
              }
            }
          }
          if (lstDetails.size()>0){
          	String[] arrDetails3 = new String[lstDetails.size()];
          	for (int j=0; j<lstDetails.size(); j++){
              Object objDet = (Object)lstDetails.get(j);
              arrDetails3[j] = objDet.toString().trim();
            }
            
          TRMSEmailSender eml = new TRMSEmailSender();
          eml.setEmailClass(arrEmailClass[i]);
          eml.setEmailCat(cat);
          eml.setReportName(reportName);
          eml.setDesc(description);
          eml.setSubject(subject);
          eml.setSummary(arrSummary2);
          eml.setDetails(arrDetails3);
          eml.setTitle(arrTitle);
          eml.setDate(date);
          eml.sendMail();
           }
        }
         String layoutId="";
        Layout selLayout = (Layout)req.getAttribute(WebKeys.LAYOUT);
        if (selLayout == null) 
          layoutId = Layout.DEFAULT_PARENT_LAYOUT_ID;
        else
          layoutId = selLayout.getLayoutId();
        System.out.println("layoutId:"+layoutId);
        PortletURLImpl portletURLImpl = new PortletURLImpl((ActionRequestImpl)req, portletId, layoutId, true);
        portletURLImpl.setWindowState(WindowState.MAXIMIZED);
        portletURLImpl.setPortletMode(PortletMode.VIEW);
        portletURLImpl.setParameter("Yr", Yr);
        portletURLImpl.setParameter("Mth", Mth);
        portletURLImpl.setParameter("Dy", Dy);
        portletURLImpl.setParameter("EmailStatus", "Y");
        portletURLImpl.setParameter("struts_action", dirpath);
        res.sendRedirect(portletURLImpl.toString());
        return;
        /**/
      }

      try {
      	if(!cmdUpdate.equals("EMAIL"))
        prefs.store();
      } catch (ValidatorException ve) {
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
    return mapping.findForward("portlet.iddm.view");
  }
}