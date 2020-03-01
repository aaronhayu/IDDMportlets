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

import com.liferay.portal.util.PortalUtil;
import com.iddm.util.PortletActionMaintenanceObject;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import com.iddm.portal.service.persistence.IDDCashInTrnst;
import com.HibernateUtil;

import java.util.Date;
import com.liferay.portal.util.PortalUtil;

import com.iddm.util.LogTracer;
import javax.portlet.PortletSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.iddm.util.Validator;
import java.util.StringTokenizer;

public class UpdateCashInTrnstAction extends PortletAction {

    public void processAction(
            ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest req, ActionResponse res)
        throws Exception {

		Validator validate = new Validator();
		String errMsg = "";
        String userId = PortalUtil.getUserId(req);
        String portletId = "19";
        String taskName = "Cash in Transit - Save";
        String portletName = "Cash in Transit";
        String backPath = "/html/common/frmError.jsp";

        LogTracer logger = new LogTracer("No."+portletId, portletName, "UpdateCashInTrnstAction.java", new Date());
        String cmd = ParamUtil.getString(req, Constants.CMD)==null?"":ParamUtil.getString(req, Constants.CMD);
        //logger.append("cmd-->"+cmd);

        if (cmd.equals(Constants.UPDATE)){

            /***************Apply Security Checking******************/
            //logger.append("Update Cash in Transit Security Check");
            //logger.append("user id " + userId);

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

            //logger.append("Parameter for data: "+ParamUtil.getString(req, "cashTrnstOut"));
            String[] cashTrnstOut = StringUtil.split(
                    ParamUtil.getString(req, "cashTrnstOut"),
                    StringPool.UNDERLINE);

            //logger.append("Parameter for data: "+ParamUtil.getString(req, "cashTrnstIn"));
            String[] cashTrnstIn = StringUtil.split(
                    ParamUtil.getString(req, "cashTrnstIn"),
                    StringPool.UNDERLINE);

            List lstcashTrnstOut = new ArrayList();
            for(int k =0;k<cashTrnstOut.length;k++) {
                String[] strcashTrnstOut = StringUtil.split(cashTrnstOut[k],StringPool.AT);
                lstcashTrnstOut.add(strcashTrnstOut);
            }

            List lstcashTrnstIn = new ArrayList();
            for(int k =0;k<cashTrnstIn.length;k++) {
                String[] strcashTrnstIn = StringUtil.split(cashTrnstIn[k],StringPool.AT);
                lstcashTrnstIn.add(strcashTrnstIn);
            }

            DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
            DateFormat format2 = new SimpleDateFormat( "yyyy-MM-dd" );
            List getExistcashTrnstIn = new ArrayList();
            List getExistcashTrnstOut = new ArrayList();

            Session hbnSession = HibernateUtil.currentSession();
            Transaction tx = hbnSession.beginTransaction();
            try {
                for(int k=0;k<lstcashTrnstIn.size();k++) {
                    String[]  strNewcashTrnstIn= (String[]) lstcashTrnstIn.get(k);

                    try{
	                    if(!validate.isValidShortDate(strNewcashTrnstIn[0])) {
							errMsg = "Invalid timestamp format ("+strNewcashTrnstIn[0]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidCurrency(strNewcashTrnstIn[1],3)) {
	     					errMsg = "Invalid currency format ("+strNewcashTrnstIn[2]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidCharacter(strNewcashTrnstIn[2],"ABCDEFGHIJKLMNOPQRSTUVWXYZ ")) {
	     					errMsg = "Invalid Inflow/Outflow format ("+strNewcashTrnstIn[2]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.DoubleOnly(strNewcashTrnstIn[3])) {
	     					errMsg = "Invalid amount format ("+strNewcashTrnstIn[3]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidTimestamp(strNewcashTrnstIn[4])) {
	     					errMsg = "Invalid timestamp format ("+strNewcashTrnstIn[4]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				}
                    } catch (Exception ex){
						errMsg = "Invalid Parameter.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}

                    //Date now1 = format.parse(strNewcashTrnstIn[4]);
                    //Date usrDtIn = format2.parse();
                    String now = strNewcashTrnstIn[4];
                    Query query = hbnSession.createQuery("select cashInTrnst from IDDCashInTrnst as cashInTrnst where cashInTrnst.primaryKey.Date = :date and cashInTrnst.primaryKey.Curr = :curr and cashInTrnst.primaryKey.Txn_type = :flow and cashInTrnst.primaryKey.Last_mod_TMS = :LmTMS");
                    query.setString("date", strNewcashTrnstIn[0]);
                    query.setString("curr", strNewcashTrnstIn[1]);
                    query.setString("flow", strNewcashTrnstIn[2]);
                    query.setString("LmTMS", now);

                    getExistcashTrnstIn = query.list();
					/*
                    System.out.println("getExistcashTrnstIn:size->"+getExistcashTrnstIn.size());
                    System.out.println("date->"+strNewcashTrnstIn[0]);
                    System.out.println("curr->"+strNewcashTrnstIn[1]);
                    System.out.println("amt->"+strNewcashTrnstIn[3]);
                    System.out.println("flow->"+strNewcashTrnstIn[2]);
                    System.out.println("LmTMS->"+now);
                    */
                    if(getExistcashTrnstIn.size()==0) {
                        IDDCashInTrnst newcashTrnstIn = new IDDCashInTrnst();
                        newcashTrnstIn.setDate(strNewcashTrnstIn[0]);
                        newcashTrnstIn.setCurr(strNewcashTrnstIn[1]);
                        newcashTrnstIn.setAmt_LC(Double.parseDouble(strNewcashTrnstIn[3]));
                        newcashTrnstIn.setTxn_type(strNewcashTrnstIn[2]);
                        newcashTrnstIn.setCrt_TMS(now);
                        newcashTrnstIn.setLast_mod_TMS(now);
                        newcashTrnstIn.setCrt_UID(userId);
                        newcashTrnstIn.setLast_mod_UID(userId);
                        newcashTrnstIn.setLast_upd_pgm("UpdateCashInTrnstAction");


                        hbnSession.save(newcashTrnstIn);
                    } else {
						errMsg = "Record already inserted into database.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}
                }

                for(int k=0;k<lstcashTrnstOut.size();k++) {
                    String[]  strNewcashTrnstOut= (String[]) lstcashTrnstOut.get(k);

                    try{
	                    if(!validate.isValidShortDate(strNewcashTrnstOut[0])) {
							errMsg = "Invalid timestamp format ("+strNewcashTrnstOut[0]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidCurrency(strNewcashTrnstOut[1],3)) {
	     					errMsg = "Invalid currency format ("+strNewcashTrnstOut[2]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidCharacter(strNewcashTrnstOut[2],"ABCDEFGHIJKLMNOPQRSTUVWXYZ ")) {
	     					errMsg = "Invalid Inflow/Outflow format ("+strNewcashTrnstOut[2]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.DoubleOnly(strNewcashTrnstOut[3])) {
	     					errMsg = "Invalid amount format ("+strNewcashTrnstOut[3]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				} else if(!validate.isValidTimestamp(strNewcashTrnstOut[4])) {
	     					errMsg = "Invalid timestamp format ("+strNewcashTrnstOut[4]+")";
	    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
	     					return;
	     				}
     				} catch (Exception ex){
						errMsg = "Invalid Parameter.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}

                    //Date now1 = format.parse(strNewcashTrnstOut[4]);
                    //Date usrDtOut = format2.parse(strNewcashTrnstOut[0]);
                    String now = strNewcashTrnstOut[4];
                    Query query = hbnSession.createQuery("select cashInTrnst from IDDCashInTrnst as cashInTrnst where cashInTrnst.primaryKey.Date = :date and cashInTrnst.primaryKey.Curr = :curr  and cashInTrnst.primaryKey.Txn_type = :flow and cashInTrnst.primaryKey.Last_mod_TMS = :LmTMS");
                    query.setString("date", strNewcashTrnstOut[0]);
                    query.setString("curr", strNewcashTrnstOut[1]);
                    query.setString("flow", strNewcashTrnstOut[2]);
                    query.setString("LmTMS", now);

                    getExistcashTrnstOut = query.list();
					/*
                    System.out.println("getExistcashTrnstOut:size->"+getExistcashTrnstOut.size());
                    System.out.println("date->"+strNewcashTrnstOut[0]);
                    System.out.println("curr->"+strNewcashTrnstOut[1]);
                    System.out.println("amt->"+strNewcashTrnstOut[3]);
                    System.out.println("flow->"+strNewcashTrnstOut[2]);
                    System.out.println("LmTMS->"+now);
                    */
                    if(getExistcashTrnstOut.size()==0) {
                        IDDCashInTrnst newcashTrnstOut = new IDDCashInTrnst();
                        newcashTrnstOut.setDate(strNewcashTrnstOut[0]);
                        newcashTrnstOut.setCurr(strNewcashTrnstOut[1]);
                        newcashTrnstOut.setAmt_LC(Double.parseDouble(strNewcashTrnstOut[3]));
                        newcashTrnstOut.setTxn_type(strNewcashTrnstOut[2]);
                        newcashTrnstOut.setCrt_TMS(now);
                        newcashTrnstOut.setLast_mod_TMS(now);
                        newcashTrnstOut.setCrt_UID(userId);
                        newcashTrnstOut.setLast_mod_UID(userId);
                        newcashTrnstOut.setLast_upd_pgm("UpdateCashInTrnstAction");

                        hbnSession.save(newcashTrnstOut);
                    } else {
						errMsg = "Record already inserted into database.";
    					res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
     					return;
					}
                }
                tx.commit();
                HibernateUtil.closeSession();
                PortletSession sess = req.getPortletSession(false);
                sess.setAttribute("cashInTrnstUpdate","Y");
                //logger.display();
            }
            catch (Exception ex) {
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
