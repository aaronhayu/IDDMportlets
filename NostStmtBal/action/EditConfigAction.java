/**
History of changes
DATE 			  By				Remarks
-------------------------------------------------------------
       Aaron              author
**/
package com.iddm.portlet.NostStmtBal.action;

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

public class EditConfigAction extends PortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
		throws Exception {

		String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
		//System.out.println("cmd-->"+cmd);

		if (cmd.equals(Constants.UPDATE)){

			PortletPreferences prefs = req.getPreferences();
			String cmdUpdate = ParamUtil.getString(req,"update");
			//System.out.println("cmdUpdate->"+cmdUpdate);

			/***************Apply Security Checking******************/
			//System.out.println("Edit Preferences Security Check");
			String userId = PortalUtil.getUserId(req);
			//System.out.println("user id " + userId);
			String portletId = "25";

            String taskName = "Nostro Statement Balance - Configure - Amount - Save";
            String portletName = "Nostro Statement Balance";
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
		 	Validator validate = new Validator();
		 	String errMsg = "";

			if(cmdUpdate.equals("AMOUNT"))
	        {
	            String tmpAmt = ParamUtil.getString(req, "table_amount");

	            //check whether the data is valid or not valid...
	            if(!validate.isValidCharacter(tmpAmt,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ")) {
	                errMsg = "Invalid amount format ("+tmpAmt+")";
	                res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	                return;
	            }
	            prefs.setValue("table_amount", tmpAmt);
	        }


			try {
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