/**
History of changes

DATE 			  By				Remarks
      Aaron      	UpdateNostStmtBal
**/
package com.iddm.portlet.NostStmtBal.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import com.iddm.portal.service.persistence.IDDStdCurr;
import com.iddm.portal.service.persistence.IDDNostStmtBal;
import com.iddm.portal.service.persistence.IDDTERASBrchProf;
import com.iddm.portal.service.persistence.IDDNostStmtBal;
import com.iddm.portal.service.persistence.IDDNostStmtBalPK;
import com.iddm.portal.service.persistence.IDDSrcSysProf;
import com.iddm.portal.service.persistence.IDDTERASCustProf;
import com.HibernateUtil;

import java.util.Date;
import com.liferay.portal.util.PortalUtil;

import com.iddm.util.LogTracer;
import javax.portlet.PortletSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.iddm.util.Validator;
import com.iddm.util.IDDExchgRateHib;


public class UpdateNostStmtBalAction extends PortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
		throws Exception {

		Validator validate = new Validator();
		String userId = PortalUtil.getUserId(req);
		String portletId = "25";
        String taskName = "Nostro Statement Balance - Save";
        String portletName = "Nostro Statement Balance";
  		String backPath = "/html/common/frmError.jsp";

        //LogTracer logger = new LogTracer("No."+portletId, portletName, "UpdateNostStmtBalAction.java", new Date());
		String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
		//System.out.println("cmd-->"+cmd);

		if (cmd.equals(Constants.UPDATE)){

			/***************Apply Security Checking******************/

			//System.out.println("Update Nostro Statement Balance Security Check");
			//System.out.println("user id " + userId);

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
		 	//System.out.println("Parameter for data: "+ParamUtil.getString(req, "NostStmtBal"));
		 	String[] NostStmtBal = StringUtil.split(ParamUtil.getString(req, "NostStmtBal"), StringPool.UNDERLINE);

		 	List lstNostStmtBal = new ArrayList();
		 	for(int k =0;k<NostStmtBal.length;k++) {
		 		String[] strNostStmtBal = StringUtil.split(NostStmtBal[k],StringPool.AT);
		 		lstNostStmtBal.add(strNostStmtBal);
		 	}

		 	DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
			List getExistNostStmtBal = new ArrayList();

			Session hbnSession = HibernateUtil.currentSession();
			String errMsg = "";

			Transaction tx = hbnSession.beginTransaction();
			try {
				for(int k=0;k<lstNostStmtBal.size();k++) {
					String[]  strNewNostStmtBal= (String[]) lstNostStmtBal.get(k);

					//System.out.println("**********Data Entry Action**********");
					Query query = hbnSession.createQuery("select cur from IDDStdCurr as cur where cur.Curr = '"+strNewNostStmtBal[4]+"'");
					List allCurr = new ArrayList();
					allCurr = query.list();
					//System.out.println("Size of allCurr==========>"+allCurr.size());

					query = hbnSession.createQuery("select b from IDDTERASBrchProf as b where b.Brch = '"+strNewNostStmtBal[1]+"'");
					List allBrch = new ArrayList();
					allBrch = query.list();
					//System.out.println("Size of allBrch==========>"+allBrch.size());

					query = hbnSession.createQuery("select distinct c.Cust_no from IDDTERASCustProf as c where c.Cust_no = '"+strNewNostStmtBal[3]+"'");
					List allCust = new ArrayList();
					allCust = query.list();
					//System.out.println("Size of allCust==========>"+allCust.size());

					query = hbnSession.createQuery("select distinct n.Nost_acct from IDDTERASNostPos as n where n.Nost_acct = '"+strNewNostStmtBal[2]+"'");
					List allNost = new ArrayList();
					allNost = query.list();
					//System.out.println("Size of allNost==========>"+allNost.size());

					//check whether the data is valid or not valid...
					try{
                        if(!validate.isValidShortDate(strNewNostStmtBal[0])) {
							errMsg = "Invalid Date format ("+strNewNostStmtBal[0]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(allBrch.size() == 0) {
	     					errMsg = "Invalid Branch code format ("+strNewNostStmtBal[1]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(allNost.size() == 0) {
	     					errMsg = "Invalid Nostro Account format ("+strNewNostStmtBal[2]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(allCust.size() == 0) {
	     					errMsg = "Invalid Customer no. format ("+strNewNostStmtBal[3]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(allCurr.size() == 0) {
	     					errMsg = "Invalid currency format ("+strNewNostStmtBal[4]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!strNewNostStmtBal[5].equals("EUROCLEAR") && !strNewNostStmtBal[5].equals("FRB")) {
	     					errMsg = "Invalid Source format ("+strNewNostStmtBal[5]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.DoubleOnly(strNewNostStmtBal[6])) {
	     					errMsg = "Invalid amount format ("+strNewNostStmtBal[6]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidTimestamp(strNewNostStmtBal[7])) {
							errMsg = "Invalid Date format ("+strNewNostStmtBal[7]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				}
					} catch (Exception ex){
						errMsg = "Invalid Parameter.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}
					//dataDate+brchCode+nost+cust+curr+src+amt+strNow
					//Date now = format.parse(strNewNostStmtBal[4]);
					String now = strNewNostStmtBal[7];
					//System.out.println("Now :"+now);
					query = hbnSession.createQuery("select NostStmtBal from IDDNostStmtBal as NostStmtBal where NostStmtBal.primaryKey.Date = :date and NostStmtBal.primaryKey.Brch_code = :brch and NostStmtBal.primaryKey.Nost_acct = :nost and NostStmtBal.primaryKey.Src = :src");
					query.setString("date", strNewNostStmtBal[0]);
					query.setString("brch", strNewNostStmtBal[1]);
					query.setString("nost", strNewNostStmtBal[2]);
					query.setString("src", strNewNostStmtBal[5]);

					getExistNostStmtBal = query.list();

					//System.out.println("getExistNostStmtBal:size->"+getExistNostStmtBal.size());
					//System.out.println("date->"+strNewNostStmtBal[0]);
					//System.out.println("brch->"+strNewNostStmtBal[1]);
					//System.out.println("nost->"+strNewNostStmtBal[2]);
					//System.out.println("src->"+strNewNostStmtBal[5]);

					if(getExistNostStmtBal.size()==0) {

						IDDNostStmtBal newNostStmtBal = new IDDNostStmtBal();
						newNostStmtBal.setDate(strNewNostStmtBal[0]);
						newNostStmtBal.setBrch_code(strNewNostStmtBal[1]);
						newNostStmtBal.setNost_acct(strNewNostStmtBal[2]);
						newNostStmtBal.setCust_no(strNewNostStmtBal[3]);
						newNostStmtBal.setCurr(strNewNostStmtBal[4]);
						newNostStmtBal.setBal_amt_LC(Double.parseDouble(strNewNostStmtBal[6]));
						newNostStmtBal.setSrc(strNewNostStmtBal[5]);
						newNostStmtBal.setCrt_TMS(now);
						newNostStmtBal.setLast_mod_TMS(now);
						newNostStmtBal.setCrt_UID(userId);
						newNostStmtBal.setLast_mod_UID(userId);
						newNostStmtBal.setLast_upd_pgm("UpdateNostStmtBalAction");

						hbnSession.save(newNostStmtBal);
					} else {
						errMsg = "Record already inserted into database.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}
				}
				tx.commit();
				HibernateUtil.closeSession();
				PortletSession sess = req.getPortletSession(false);
				sess.setAttribute("NostStmtBalUpdate","Y");
				//logger.display();
			} catch (Exception ex) {
				System.out.println("Exception:"+ex);
				//logger.display();
				if (tx != null) tx.rollback();
						throw ex;
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
