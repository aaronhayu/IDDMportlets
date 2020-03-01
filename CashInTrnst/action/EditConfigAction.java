package com.iddm.portlet.CashInTrnst.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;

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
import java.util.StringTokenizer;
import org.hibernate.*;
import com.iddm.portal.service.persistence.IDDTrnstFltr;
import com.HibernateUtil;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditConfigAction extends PortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
		throws Exception {

		Validator validate = new Validator();
		String errMsg = "";

		String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
		//System.out.println("cmd-->"+cmd);

		if (!cmd.equals(Constants.UPDATE))
		{
			mapping.findForward("portlet.iddm.CashInTrnst.view");
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
		String portletId = "19";
		String taskName = "Cash In Transit - Configure - Save";
  		String portletName = "Cash In Transit";
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

	 		if(cmdUpdate.equals("CURRENCY"))
			{
		       String setting = ParamUtil.getString(req, "strSetting")==null?"":ParamUtil.getString(req, "strSetting");
		       String layoutId = ParamUtil.getString(req, "layoutId")==null?"":ParamUtil.getString(req, "layoutId");
		       //System.out.println("setting : " + setting);
		       //System.out.println("layoutId : " + layoutId);
    		   StringTokenizer st = new StringTokenizer(setting,"|");
    		   StringTokenizer st1 = null;
    		   //System.out.println("Count : " + st.countTokens());
			   String[] rawData = new String[st.countTokens()];

               if(!validate.isValidCharacter(setting,"ABCDEFGHIJKLMNOPQRSTUVWXYZ()|_1234567890 ")) {
		          errMsg = "Invalid Preference item format (\""+setting+"\")";
		          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
		          return;
	           }

			   int i = 0;
			   int j = 0;
			   String curr = "";
			   String custno = "";
			   Query query = null;
        	   Date now = new Date();

			   List getExistTrnstFltr = new ArrayList();
			   Session hbnSession = HibernateUtil.currentSession();
			   Transaction tx = hbnSession.beginTransaction();
			   try {

 				 query = hbnSession.createQuery("select A from IDDTrnstFltr as A where A.primaryKey.Portlet_Id = :portId and A.primaryKey.Usr_Id = :usrId and A.primaryKey.Layout_Id =:layId");
  				 query.setString("portId", portletId);
  				 query.setString("usrId", userId);
  				 query.setString("layId", layoutId);

                 getExistTrnstFltr = query.list();
 	             //System.out.println("getExistTrnstFltr size : " + getExistTrnstFltr.size());

	             if(getExistTrnstFltr.size()>0) {
	             	//List QueryList = new ArrayList();
  				    //QueryList = hbnSession.createSQLQuery("Delete from IDDTrnstFltr A Where A.Portlet_Id ='" + portletId +"' and A.Usr_Id = '"+userId+"' and A.Layout_Id ='" +layoutId+"'").list();
  				    //query1.setString("portId", portletId);
  				    //query1.setString("usrId", userId);
  				    //query1.setString("layId", layoutId);
                	for(int m=0; m<getExistTrnstFltr.size(); m++){
                    	IDDTrnstFltr existTrnstFltr = (IDDTrnstFltr) getExistTrnstFltr.get(m);
						hbnSession.delete(existTrnstFltr);
					}

				 }

  				 Query query2 = hbnSession.createQuery("select A from IDDTrnstFltr as A where A.primaryKey.Portlet_Id = :portId and A.primaryKey.Usr_Id = :usrId and A.primaryKey.Layout_Id =:layId");
  				 query2.setString("portId", portletId);
  				 query2.setString("usrId", userId);
  				 query2.setString("layId", layoutId);
				 getExistTrnstFltr = query2.list();

				 if(getExistTrnstFltr.size()==0) {

			        while (st.hasMoreTokens()) {
			           rawData[i]=st.nextToken();

			           st1 = new StringTokenizer(rawData[i],"_");
			           j= 0;
			           while (st1.hasMoreTokens()) {
			      	      if (j == 0)
			      	         curr = st1.nextToken();
			      	      else
			      	         custno = st1.nextToken();

			      	      j++;
			           }

 	               	   //System.out.println("portletId " +portletId );
 	               	   //System.out.println("userId " +userId );
 	               	   //System.out.println("layoutId " +layoutId );
 	               	   //System.out.println("curr " +curr );
 	               	   //System.out.println("custno " +custno );
 	               	   //System.out.println("now " +now );
					   IDDTrnstFltr newTrnstFltr = new IDDTrnstFltr();
					   newTrnstFltr.setPortlet_Id(portletId);
					   newTrnstFltr.setUsr_Id(userId);
					   newTrnstFltr.setLayout_Id(layoutId);
					   newTrnstFltr.setCurr(curr);
					   newTrnstFltr.setCust(custno);
					   newTrnstFltr.setCrt_TMS(now);
					   newTrnstFltr.setLast_mod_TMS(now);
					   newTrnstFltr.setCrt_UID(userId);
					   newTrnstFltr.setLast_mod_UID(userId);
					   newTrnstFltr.setLast_upd_pgm("EditConfigAction");

					   hbnSession.save(newTrnstFltr);

			           i++;
  			        }
				 } else {
					errMsg = "Insert Records into database failed.";
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     				return;
				 }

  			   } catch (Exception ex) {
				  if (tx != null) tx.rollback();
					throw ex;
			   }

			   tx.commit();
			   HibernateUtil.closeSession();
			}
			else if(cmdUpdate.equals("DATE"))
			{
				String date = ParamUtil.getString(req, "cfgDate");

				//check whether the data is valid or not valid...
				if(!validate.isValidShortDate(date)) {
					errMsg = "Invalid Date format ("+date+")";
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     				return;
     			}

				prefs.setValue("table_date",date);
			}
             else if(cmdUpdate.equals("AMOUNT"))
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

		return mapping.findForward("portlet.iddm.view");
	}

}