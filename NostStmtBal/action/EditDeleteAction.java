/**
History of changes

DATE 			  By				Remarks
-------------------------------------------------------------
       Aaron      Edit And Delete Action
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

public class EditDeleteAction extends PortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
		throws Exception {

		Validator validate = new Validator();
		String userId = PortalUtil.getUserId(req);
		String portletId = "25";
  		String portletName = "Nostro Statement Balance";
  		String backPath = "/html/common/frmError.jsp";

		LogTracer logger = new LogTracer("No."+portletId, portletName, "EditDeleteAction.java", new Date());

		String firstTime = ParamUtil.getString(req, "firstTime");
		String countRec = ParamUtil.getString(req, "countRec");
		String recordNo = ParamUtil.getString(req, "recordNum");
		String PkDate = ParamUtil.getString(req, "pkDate");
		String newDate = ParamUtil.getString(req, "selectDate");
	 	String PkNostAcct = ParamUtil.getString(req, "pknost");
	 	String NostAcct = ParamUtil.getString(req, "nost");
	 	String currency = ParamUtil.getString(req, "curr");
	 	String Pksource = ParamUtil.getString(req, "pksrc");
	 	String source = ParamUtil.getString(req, "src");
	 	String Pkbranch = ParamUtil.getString(req, "pkbrch");
	 	String branch = ParamUtil.getString(req, "brch");
	 	String customer = ParamUtil.getString(req, "cust");
	 	String recSize = ParamUtil.getString(req, "paramRecSize");
	 	String Amount = ParamUtil.getString(req, "amt");
	 	String CrtTms = ParamUtil.getString(req, "crtTms");
		String cmd = ParamUtil.getString(req,"mode")==null?"":ParamUtil.getString(req, "mode");

		//System.out.println("dt----->"+PkDate);
		//System.out.println("brch----->"+Pkbranch);
		//System.out.println("nostAcct----->"+PkNostAcct);
		//System.out.println("src----->"+Pksource);

		//logger.append("cmd-->"+cmd);

		if (cmd.equals("EDIT")){

			/***************Apply Security Checking******************/
			String taskName = "Nostro Statement Balance - Edit";

			try{
  				PortletActionMaintenanceObject mObj = new PortletActionMaintenanceObject();

  				//logger.append("Check Object Init");
  				if(mObj.objectInit(userId, portletName, backPath) == false) {
     				//logger.append("Maintenance Object Init Failed");
     				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
     				return;
     			}

  				//logger.append("Check Access Time");
  				if (mObj.checkAccessTime() == false) {
    				//logger.append("Access Time Invalid");
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmSystemFail.jsp");
     				return;
     			}

  				if (mObj.checkAuthorized(userId, portletId, portletName) == false){
    				//logger.append("Unauthorized User ...." + userId);
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAccessDenied.jsp");
     				return;
				}

  				if (mObj.checkTaskAssignment(taskName) == false) {
	  				//logger.append("No Task Assign to access the page " + userId + taskName);
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmTaskDenied.jsp");
     				return;
				}

  				if (mObj.writeAuditTrail(taskName, userId) == false) {
	  				//logger.append("Insert Audit Trail Failed");
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuditTrailFail.jsp");
     				return;
				}

			}catch(Exception e){
		 		e.printStackTrace();
		 		res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
		 		return;
		 	}

		 	/***************End Security Checking******************/

		 	//To get current time to update to database update timestamp
			DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" ); // to insert into database
			Date now = new Date();
			String tmpNow = "";
			tmpNow = format.format(now);

			Session hbnSession = HibernateUtil.currentSession();
			String errMsg = "";

			//System.out.println("**********Edit Delete Action**********");
			Query query = hbnSession.createQuery("select cur from IDDStdCurr as cur where cur.Curr = '"+currency+"'");
			List allCurr = new ArrayList();
			allCurr = query.list();
			//System.out.println("Size of allCurr==========>"+allCurr.size());

			query = hbnSession.createQuery("select a from IDDTERASBrchProf as a where a.Brch = '"+Pkbranch+"'");
			List allPkBrch = new ArrayList();
			allPkBrch = query.list();
			//System.out.println("Size of allPkBrch==========>"+allPkBrch.size());

			query = hbnSession.createQuery("select b from IDDTERASBrchProf as b where b.Brch = '"+branch+"'");
			List allBrch = new ArrayList();
			allBrch = query.list();
			//System.out.println("Size of allBrch==========>"+allBrch.size());

			query = hbnSession.createQuery("select distinct c.Cust_no from IDDTERASCustProf as c where c.Cust_no = '"+customer+"'");
			List allCust = new ArrayList();
			allCust = query.list();
			//System.out.println("Size of allCust==========>"+allCust.size());

			query = hbnSession.createQuery("select distinct d.Nost_acct from IDDTERASNostPos as d where d.Nost_acct = '"+NostAcct+"'");
			List allNost = new ArrayList();
			allNost = query.list();
			//System.out.println("Size of allNost==========>"+allNost.size());

			query = hbnSession.createQuery("select distinct n.Nost_acct from IDDTERASNostPos as n where n.Nost_acct = '"+PkNostAcct+"'");
			List allPkNost = new ArrayList();
			allPkNost = query.list();
			//System.out.println("Size of allPkNost==========>"+allPkNost.size());

			Transaction tx = hbnSession.beginTransaction();
			try {
				//check whether the data is valid or not valid...
				try{
					if(!validate.isValidShortDate(PkDate)) {
     					errMsg = "Invalid Date format ("+PkDate+")";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
     				} else if(!validate.isValidShortDate(newDate)) {
						errMsg = "Invalid Date format ("+newDate+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allBrch.size() == 0) {
	     				errMsg = "Invalid Branch code format ("+branch+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allPkBrch.size() == 0) {
	     				errMsg = "Invalid Branch code format ("+Pkbranch+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allNost.size() == 0) {
	     				errMsg = "Invalid Nostro Account format ("+NostAcct+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allPkNost.size() == 0) {
	     				errMsg = "Invalid Nostro Account format ("+PkNostAcct+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allCust.size() == 0) {
	     				errMsg = "Invalid Customer no. format ("+customer+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(allCurr.size() == 0) {
	     				errMsg = "Invalid currency format ("+currency+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!source.equals("EUROCLEAR") && !source.equals("FRB")) {
	     				errMsg = "Invalid Source format ("+source+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!Pksource.equals("EUROCLEAR") && !Pksource.equals("FRB")) {
	     				errMsg = "Invalid Source format ("+Pksource+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.DoubleOnly(Amount)) {
	     				errMsg = "Invalid amount format ("+Amount+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			}
				} catch (Exception ex){
					errMsg = "Invalid Parameter.";
					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
 					return;
				}

				//Query query = hbnSession.createQuery("select incBdgt from IDDNostStmtBal as incBdgt where incBdgt.primaryKey.Month = :mth and incBdgt.primaryKey.Data_leaf_ID = :id and incBdgt.primaryKey.Curr = :cur");
				query = hbnSession.createQuery("select NostStmtBal from IDDNostStmtBal as NostStmtBal where NostStmtBal.primaryKey.Date = :dt and NostStmtBal.primaryKey.Brch_code = :brch and NostStmtBal.primaryKey.Nost_acct = :nostAcct and NostStmtBal.primaryKey.Src = :src");
				query.setString("dt", PkDate);
				query.setString("brch", Pkbranch);
				query.setString("nostAcct", PkNostAcct);
				query.setString("src", Pksource);

                List getExistNostStmtBal = query.list();

				//System.out.println("getExistNostStmtBal:size->"+getExistNostStmtBal.size());

                if(getExistNostStmtBal.size()>0) {
                	IDDNostStmtBal existNostStmtBal = (IDDNostStmtBal) getExistNostStmtBal.get(0);
					hbnSession.delete(existNostStmtBal);

					//System.out.println("Record is deleted!!!");

					tx.commit();
					HibernateUtil.closeSession();
					//logger.display();
				} else {
					errMsg = "Record not exist.";
					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
 					return;
				}
				Session hbnSession2 = HibernateUtil.currentSession();
				Transaction tx2 = hbnSession2.beginTransaction();

				if(getExistNostStmtBal.size()>0) {
					IDDNostStmtBal newNostStmtBal = new IDDNostStmtBal();
					newNostStmtBal.setDate(newDate);
					newNostStmtBal.setNost_acct(PkNostAcct);
					newNostStmtBal.setCurr(currency);
					newNostStmtBal.setCust_no(customer);
					newNostStmtBal.setSrc(Pksource);
					newNostStmtBal.setBrch_code(Pkbranch);
					newNostStmtBal.setBal_amt_LC(Double.parseDouble(Amount));
					newNostStmtBal.setCrt_TMS(CrtTms);
					newNostStmtBal.setLast_mod_TMS(tmpNow);
					newNostStmtBal.setCrt_UID(userId);
					newNostStmtBal.setLast_mod_UID(userId);
					newNostStmtBal.setLast_upd_pgm("EditDeleteAction");

					hbnSession2.save(newNostStmtBal);
					tx2.commit();
					HibernateUtil.closeSession();
					//logger.display();
				}
			} catch (Exception ex) {
				logger.append("Exception:"+ex);
				if (tx != null) tx.rollback();
						throw ex;
			}
		} else if (cmd.equals("DELETE")){

			String taskName = "Nostro Statement Balance - Delete";

			try{
  				PortletActionMaintenanceObject mObj = new PortletActionMaintenanceObject();

  				//logger.append("Check Object Init");
  				if(mObj.objectInit(userId, portletName, backPath) == false) {
     				//logger.append("Maintenance Object Init Failed");
     				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
     				return;
     			}

  				//logger.append("Check Access Time");
  				if (mObj.checkAccessTime() == false) {
    				//logger.append("Access Time Invalid");
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmSystemFail.jsp");
     				return;
     			}

  				if (mObj.checkAuthorized(userId, portletId, portletName) == false){
    				//logger.append("Unauthorized User ...." + userId);
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAccessDenied.jsp");
     				return;
				}

  				if (mObj.checkTaskAssignment(taskName) == false) {
	  				//logger.append("No Task Assign to access the page " + userId + taskName);
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmTaskDenied.jsp");
     				return;
				}

  				if (mObj.writeAuditTrail(taskName, userId) == false) {
	  				//logger.append("Insert Audit Trail Failed");
    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuditTrailFail.jsp");
     				return;
				}

			}catch(Exception e){
		 		e.printStackTrace();
		 		res.sendRedirect("/iddm/html/portlet/iddm/common/frmAuthFail.jsp");
		 		return;
		 	}

		 	/***************End Security Checking******************/

		 	//To get current time to update to database update timestamp
			DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" ); // to insert into database
			Date now = new Date();
			String tmpNow = "";
			tmpNow = format.format(now);

			Session hbnSession = HibernateUtil.currentSession();
			String errMsg = "";

			Transaction tx = hbnSession.beginTransaction();
			try {
				//check whether the data is valid or not valid...
				try{
                    if(!validate.isValidShortDate(PkDate)) {
     					errMsg = "Invalid Date format ("+PkDate+")";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
	     			} else if(!validate.checkValidCharacter(Pkbranch,"0123456789")) {
	     				errMsg = "Invalid Branch code format ("+Pkbranch+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.checkValidCharacter(PkNostAcct,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")) {
	     				errMsg = "Invalid Nostro Account format ("+PkNostAcct+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.checkValidCharacter(Pksource,"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")) {
	     				errMsg = "Invalid Source format ("+Pksource+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			}
				} catch (Exception ex){
					errMsg = "Invalid Parameter.";
					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
 					return;
				}

				//Query query = hbnSession.createQuery("select incBdgt from IDDNostStmtBal as incBdgt where incBdgt.primaryKey.Month = :mth and incBdgt.primaryKey.Data_leaf_ID = :id and incBdgt.primaryKey.Curr = :cur");
				Query query = hbnSession.createQuery("select NostStmtBal from IDDNostStmtBal as NostStmtBal where NostStmtBal.primaryKey.Date = :dt and NostStmtBal.primaryKey.Brch_code = :brch and NostStmtBal.primaryKey.Nost_acct = :nostAcct and NostStmtBal.primaryKey.Src = :src");
				query.setString("dt", PkDate);
				query.setString("brch", Pkbranch);
				query.setString("nostAcct", PkNostAcct);
				query.setString("src", Pksource);

                List getExistNostStmtBal = query.list();

                //System.out.println("getExistNostStmtBal:size->"+getExistNostStmtBal.size());

                if(getExistNostStmtBal.size()>0) {
                    IDDNostStmtBal existNostStmtBal = (IDDNostStmtBal) getExistNostStmtBal.get(0);
					hbnSession.delete(existNostStmtBal);
				} else {
					errMsg = "Record not exist.";
					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
 					return;
				}
				tx.commit();
				HibernateUtil.closeSession();
				//logger.display();
			} catch (Exception ex) {
				logger.append("Exception:"+ex);
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
