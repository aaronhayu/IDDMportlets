/**
History of changes
DATE 			  By				Remarks
-------------------------------------------------------------
      Aaron      Edit And Delete Action
**/
package com.iddm.portlet.CashInTrnst.action;

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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import com.liferay.portal.util.PortalUtil;
import com.iddm.util.PortletActionMaintenanceObject;
import com.iddm.portal.service.persistence.IDDStdCurr;
import com.iddm.portal.service.persistence.IDDCashInTrnst;
import com.iddm.portal.service.persistence.IDDCashInTrnstPK;
import com.iddm.portal.service.persistence.IDDTxnTypeProf;
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
		String portletId = "19";
  		String portletName = "Cash In Transit";
  		String backPath = "/html/common/frmError.jsp";

		LogTracer logger = new LogTracer("No."+portletId, portletName, "EditDeleteAction.java", new Date());
		String cmd = ParamUtil.getString(req,"mode")==null?"":ParamUtil.getString(req, "mode");
		//logger.append("cmd-->"+cmd);

		String PkDate = ParamUtil.getString(req, "pkDate");
		String currency = ParamUtil.getString(req, "currency");
		String r_amt = ParamUtil.getString(req, "r_amt");
		String p_amt = ParamUtil.getString(req, "p_amt");
		Date today = new Date();
        /*
        System.out.println("==========cmd:"+cmd);
	 	System.out.println("Cash In Transit=====>Edit&Delete Parameter");
	 	System.out.println("==========PkDate:"+PkDate);
	 	System.out.println("==========currency:"+currency);
	 	System.out.println("==========r_amt:"+r_amt);
	 	System.out.println("==========p_amt:"+p_amt);
        */
        //To get current time to update to database update timestamp
        DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" ); // to insert into database
  		String now = format.format(today);
		if (cmd.equals("EDIT")){

		    /***************Apply Security Checking******************/
		    String taskName = "Cash In Transit - Edit";

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
            String outflow_pAmt = ParamUtil.getString(req, "outflow_pAmt");
            String inflow_rAmt = ParamUtil.getString(req, "inflow_rAmt");
            //System.out.println("==========outflow_pAmt:"+outflow_pAmt);
            //System.out.println("==========inflow_rAmt:"+inflow_rAmt);

			Session hbnSession = HibernateUtil.currentSession();
			String errMsg = "";

			Transaction tx = hbnSession.beginTransaction();
			try {
				//check whether the data is valid or not valid...
					if(!validate.isValidShortDate(PkDate)) {
     					errMsg = "Invalid Date format ("+PkDate+")";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
     				} else if(!validate.isValidCurrency(currency,3)) {
	     				errMsg = "Invalid Currency format ("+currency+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.DoubleOnly(r_amt)) {
	     				errMsg = "Invalid inflow amount format ("+r_amt+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.DoubleOnly(p_amt)) {
	     				errMsg = "Invalid outflow amount format ("+p_amt+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.DoubleOnly(inflow_rAmt)) {
	     				errMsg = "Invalid inflow amount format ("+inflow_rAmt+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			} else if(!validate.DoubleOnly(outflow_pAmt)) {
	     				errMsg = "Invalid outflow amount format ("+outflow_pAmt+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			}

	            double newR_amt = Double.parseDouble(inflow_rAmt)-Double.parseDouble(r_amt);
    	        double newP_amt = Double.parseDouble(outflow_pAmt)-Double.parseDouble(p_amt);

                //System.out.println("newR_amt"+newR_amt);
                //System.out.println("newP_amt"+newP_amt);
                //System.out.println("PkDate"+PkDate);
                //System.out.println("currency"+currency);

                Double dbVal = (Double) hbnSession.createQuery("select sum(CashInTrnst.Amt_LC) from IDDCashInTrnst as CashInTrnst where CashInTrnst.primaryKey.Date = :dt and CashInTrnst.primaryKey.Curr = :currency and CashInTrnst.primaryKey.Txn_type = :txn group by CashInTrnst.primaryKey.Txn_type,CashInTrnst.primaryKey.Date,CashInTrnst.primaryKey.Curr")
				.setString("dt", PkDate)
				.setString("currency", currency)
				.setString("txn", "P")
                .uniqueResult();

                double dbP_rm = 0.00;
                try{
                    dbP_rm = dbVal.doubleValue();
                } catch (Exception ex){
                    dbP_rm = 0.00;
                }
                //System.out.println("dbP_rm----->"+dbP_rm);

                Double dbValr = (Double) hbnSession.createQuery("select sum(CashInTrnst.Amt_LC) from IDDCashInTrnst as CashInTrnst where CashInTrnst.primaryKey.Date = :dt and CashInTrnst.primaryKey.Curr = :currency and CashInTrnst.primaryKey.Txn_type = :txn group by CashInTrnst.primaryKey.Txn_type,CashInTrnst.primaryKey.Date,CashInTrnst.primaryKey.Curr")
				.setString("dt", PkDate)
				.setString("currency", currency)
				.setString("txn", "R")
                .uniqueResult();

                double dbR_rm = 0.00;
                try{
                    dbR_rm = dbValr.doubleValue();
                }catch (Exception ex){
                    dbR_rm  = 0.00;
                }

				//System.out.println("dbR_rm----->"+dbR_rm);
                if(dbR_rm!=Double.parseDouble(inflow_rAmt)){
                    if(newR_amt != 0){
                    	IDDCashInTrnst newCashInTrnst = new IDDCashInTrnst();
						newCashInTrnst.setDate(PkDate);
						newCashInTrnst.setCurr(currency);
						newCashInTrnst.setTxn_type("R");
						newCashInTrnst.setAmt_LC(newR_amt);
						newCashInTrnst.setCrt_TMS(now);
						newCashInTrnst.setLast_mod_TMS(now);
						newCashInTrnst.setCrt_UID(userId);
						newCashInTrnst.setLast_mod_UID(userId);
						newCashInTrnst.setLast_upd_pgm("EditDeleteAction");
						hbnSession.save(newCashInTrnst);
					}
				}
				if(dbP_rm!=Double.parseDouble(outflow_pAmt)) {
					if(newP_amt != 0){
						IDDCashInTrnst newCashInTrnst = new IDDCashInTrnst();
						newCashInTrnst.setDate(PkDate);
						newCashInTrnst.setCurr(currency);
						newCashInTrnst.setTxn_type("P");
						newCashInTrnst.setAmt_LC(newP_amt);
	                    newCashInTrnst.setCrt_TMS(now);
						newCashInTrnst.setLast_mod_TMS(now);
						newCashInTrnst.setCrt_UID(userId);
						newCashInTrnst.setLast_mod_UID(userId);
						newCashInTrnst.setLast_upd_pgm("EditDeleteAction");
						hbnSession.save(newCashInTrnst);
					}
				}
				tx.commit();
				HibernateUtil.closeSession();
				//logger.display();
			} catch (Exception ex) {
				logger.append("Exception:"+ex);
				//logger.display();
				if (tx != null) tx.rollback();
						throw ex;
			}

		} else if (cmd.equals("DELETE")){

	    	String taskName = "Cash In Transit - Delete";

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

			Session hbnSession = HibernateUtil.currentSession();
			String errMsg = "";

			Transaction tx = hbnSession.beginTransaction();
			try {
				//check whether the data is valid or not valid...
                    if(!validate.isValidShortDate(PkDate)) {
     					errMsg = "Invalid Date format ("+PkDate+")";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
	     			} else if(!validate.isValidCurrency(currency,3)) {
	     				errMsg = "Invalid Currency format ("+currency+")";
	    				res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     				return;
	     			}

				Query query = hbnSession.createQuery("select IDDCashInTrnst from IDDCashInTrnst as IDDCashInTrnst where IDDCashInTrnst.primaryKey.Date = :dt and IDDCashInTrnst.primaryKey.Curr = :currency ");
				query.setString("dt", PkDate);
				query.setString("currency", currency);

				//System.out.println("dt----->"+PkDate);
				//System.out.println("currency----->"+currency);

                List getExistCashInTrnst = query.list();

                //System.out.println("getExistCashInTrnst:size->"+getExistCashInTrnst.size());

                if(getExistCashInTrnst.size()>0) {
                	for(int i=0; i<getExistCashInTrnst.size(); i++){
                    	IDDCashInTrnst existCashInTrnst = (IDDCashInTrnst) getExistCashInTrnst.get(i);
						hbnSession.delete(existCashInTrnst);
					}
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
