package com.iddm.portlet.HoldLmtMaint.action;

import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.util.ParamUtil;
import com.liferay.util.StringPool;
import com.liferay.util.StringUtil;

import java.util.List;
import java.util.ArrayList;

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
import org.hibernate.Hibernate;
import com.iddm.portal.service.persistence.IDDTERASCPLimit;
import com.iddm.portal.service.persistence.IDDExpsrPct;
import com.iddm.portal.service.persistence.IDDHldgPct;
import com.HibernateUtil;

import java.util.Date;

import com.iddm.util.LogTracer;
import javax.portlet.PortletSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.iddm.util.Validator;

public class HoldLmtMaintAction extends PortletAction {
  private String ErrMsg="";
  public void processAction(
    ActionMapping mapping, ActionForm form, PortletConfig config,
    ActionRequest req, ActionResponse res)
      throws Exception {
    Validator validate = new Validator();
    String userId = PortalUtil.getUserId(req);
    String portletId = "108";
    String taskName = "",taskName2="";
    String portletName = "Holding Limit Maintenance";
    String backPath = "/html/common/frmError.jsp";
    String errMsg="";

    String mode = ParamUtil.getString(req, "mode")==null?"":ParamUtil.getString(req, "mode");

    if ("LmtAmt_ADD".equalsIgnoreCase(mode) ||"LmtAmt_EDIT".equalsIgnoreCase(mode)){
      taskName="Holding Limit Maintenance - Limit Amomunt - Save";
      taskName2="Holding Limit Maintenance - Limit Amomunt - Save";
    }else if ("ExposurePct_ADD".equalsIgnoreCase(mode) || "ExposurePct_EDIT".equalsIgnoreCase(mode)){
      taskName="Holding Limit Maintenance - Exposure % - Save";
      taskName2="Holding Limit Maintenance - Exposure Percentage - Save";
    }else if ("LmtPct_ADD".equalsIgnoreCase(mode) || "LmtPct_EDIT".equalsIgnoreCase(mode)){
      taskName="Holding Limit Maintenance - Limit % - Save";
      taskName2="Holding Limit Maintenance - Limit Percentage - Save";
    }

    System.out.println("portletId "+portletId);
    System.out.println("portletName "+portletName);
    System.out.println("mode "+mode);
    /***************Apply Security Checking******************/
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

       
    //-------------------------- Limit Amount --> Edit ----------------------------------
    if ("LmtAmt_EDIT".equalsIgnoreCase(mode)){
      Session hbnSession = HibernateUtil.currentSession();
      String sql="";
      String Eff_date = ParamUtil.getString(req, "Eff_date")==null?"":ParamUtil.getString(req, "Eff_date");
      String Last_mod_TMS = ParamUtil.getString(req, "Last_mod_TMS")==null?"":ParamUtil.getString(req, "Last_mod_TMS");
      String Grp_ID = ParamUtil.getString(req, "ID")==null?"":ParamUtil.getString(req, "ID");
      String NewEffDate = ParamUtil.getString(req, "NewEffDate")==null?"":ParamUtil.getString(req, "NewEffDate");
      String bla=ParamUtil.getString(req, "bla")==null?"":ParamUtil.getString(req, "bla");
      String bgl=ParamUtil.getString(req, "bgl")==null?"":ParamUtil.getString(req, "bgl");
      String rla=ParamUtil.getString(req, "rla")==null?"":ParamUtil.getString(req, "rla");
      String rgl=ParamUtil.getString(req, "rgl")==null?"":ParamUtil.getString(req, "rgl");
      String dla=ParamUtil.getString(req, "dla")==null?"":ParamUtil.getString(req, "dla");
      String dgl=ParamUtil.getString(req, "dgl")==null?"":ParamUtil.getString(req, "dgl");
      String fpla=ParamUtil.getString(req, "fpla")==null?"":ParamUtil.getString(req, "fpla");
      String fpgl=ParamUtil.getString(req, "fpgl")==null?"":ParamUtil.getString(req, "fpgl");
      String fsla=ParamUtil.getString(req, "fsla")==null?"":ParamUtil.getString(req, "fsla");
      String fsgl=ParamUtil.getString(req, "fsgl")==null?"":ParamUtil.getString(req, "fsgl");
      String gdla=ParamUtil.getString(req, "gdla")==null?"":ParamUtil.getString(req, "gdla");
      String gdgl=ParamUtil.getString(req, "gdgl")==null?"":ParamUtil.getString(req, "gdgl");
      String gola=ParamUtil.getString(req, "gola")==null?"":ParamUtil.getString(req, "gola");
      String gogl=ParamUtil.getString(req, "gogl")==null?"":ParamUtil.getString(req, "gogl");
      String ndla=ParamUtil.getString(req, "ndla")==null?"":ParamUtil.getString(req, "ndla");
      String ndgl=ParamUtil.getString(req, "ndgl")==null?"":ParamUtil.getString(req, "ndgl");
      String nbla=ParamUtil.getString(req, "nbla")==null?"":ParamUtil.getString(req, "nbla");
      String nbgl=ParamUtil.getString(req, "nbgl")==null?"":ParamUtil.getString(req, "nbgl");
      String nrla=ParamUtil.getString(req, "nrla")==null?"":ParamUtil.getString(req, "nrla");
      String nrgl=ParamUtil.getString(req, "nrgl")==null?"":ParamUtil.getString(req, "nrgl");
      String ldla=ParamUtil.getString(req, "ldla")==null?"":ParamUtil.getString(req, "ldla");
      String ldgl=ParamUtil.getString(req, "ldgl")==null?"":ParamUtil.getString(req, "ldgl");
      String lbla=ParamUtil.getString(req, "lbla")==null?"":ParamUtil.getString(req, "lbla");
      String lbgl=ParamUtil.getString(req, "lbgl")==null?"":ParamUtil.getString(req, "lbgl");
      String lrla=ParamUtil.getString(req, "lrla")==null?"":ParamUtil.getString(req, "lrla");
      String lrgl=ParamUtil.getString(req, "lrgl")==null?"":ParamUtil.getString(req, "lrgl");
      String fcr=ParamUtil.getString(req, "fcr")==null?"":ParamUtil.getString(req, "fcr");
      String mcr=ParamUtil.getString(req, "mcr")==null?"":ParamUtil.getString(req, "mcr");
      String fsq=ParamUtil.getString(req, "fsq")==null?"":ParamUtil.getString(req, "fsq");
      String msq=ParamUtil.getString(req, "msq")==null?"":ParamUtil.getString(req, "msq");

      String temp_bla=ParamUtil.getString(req, "temp_bla")==null?"":ParamUtil.getString(req, "temp_bla");
      String temp_bgl=ParamUtil.getString(req, "temp_bgl")==null?"":ParamUtil.getString(req, "temp_bgl");
      String temp_rla=ParamUtil.getString(req, "temp_rla")==null?"":ParamUtil.getString(req, "temp_rla");
      String temp_rgl=ParamUtil.getString(req, "temp_rgl")==null?"":ParamUtil.getString(req, "temp_rgl");

      String bond_effdate=ParamUtil.getString(req, "bond_effdate")==null?"":ParamUtil.getString(req, "bond_effdate");
      String repo_effdate=ParamUtil.getString(req, "repo_effdate")==null?"":ParamUtil.getString(req, "repo_effdate");
      String bond_date=ParamUtil.getString(req, "bond_date")==null?"":ParamUtil.getString(req, "bond_date");
      String repo_date=ParamUtil.getString(req, "repo_date")==null?"":ParamUtil.getString(req, "repo_date");

      System.out.println("Eff_date "+Eff_date);
      System.out.println("Last_mod_TMS "+Last_mod_TMS);
      System.out.println("ID "+Grp_ID);
      System.out.println("NewEffDate "+NewEffDate);
      //---check mandatory & integrity
      /*if (!validate.isValidShortDate(Eff_date)){
        errMsg = "Invalid Effective Date Format ("+Eff_date+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }*/
      /*if (!validate.isValidShortDate(NewEffDate)&&!"NULL".equals(NewEffDate)){
        errMsg = "Invalid New Effective Date Format ("+NewEffDate+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }*/
      if(!validate.isValidTimestamp(Last_mod_TMS)) {
        errMsg = "Invalid timestamp format ("+Last_mod_TMS+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }
      //---check record whether been modified
      Transaction tx = hbnSession.beginTransaction();
      DateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
      try{
        Query query = hbnSession.createQuery("select x from IDDTERASCPLimit as x where Grp_ID='"+Grp_ID+"' and Last_mod_TMS = '"+Last_mod_TMS+"'");
        List lstRec = query.list();
        String tms = getTMS(1);
        if (lstRec.size()>0){
          IDDTERASCPLimit updRec = (IDDTERASCPLimit)lstRec.get(0);
          /*if (!"NULL".equals(NewEffDate)){
            //Insert
            IDDTERASCPLimit newRec = new IDDTERASCPLimit();
            //newRec.setDate(NewEffDate+" 00:00:00.0");
            newRec.setGrp_ID(Grp_ID);
            if (!"".equals(bla))
              newRec.setBond_limit_amt(bla);
            else
              newRec.setBond_limit_amt(null);

            if (!"".equals(bgl))
              newRec.setBond_gross_limit(bgl);
            else
              newRec.setBond_gross_limit(null);

            if (!"".equals(rla))
              newRec.setRepo_limit_amt(rla);
            else
              newRec.setRepo_limit_amt(null);

            if (!"".equals(rgl))
              newRec.setRepo_gross_limit(rgl);
            else
              newRec.setRepo_gross_limit(null);

            if (!"".equals(dla))
              newRec.setDepo_limit_amt(dla);
            else
              newRec.setDepo_limit_amt(null);

            if (!"".equals(dgl))
              newRec.setDepo_gross_limit(dgl);
            else
              newRec.setDepo_gross_limit(null);

            if (!"".equals(fpla))
              newRec.setFX_presetl_limit_amt(fpla);
            else
              newRec.setFX_presetl_limit_amt(null);

            if (!"".equals(fpgl))
              newRec.setFX_presetl_gross_limit(fpgl);
            else
              newRec.setFX_presetl_gross_limit(null);

            if (!"".equals(fsla))
              newRec.setFX_setl_limit_amt(fsla);
            else
              newRec.setFX_setl_limit_amt(null);

            if (!"".equals(fsgl))
              newRec.setFX_setl_gross_limit(fsgl);
            else
              newRec.setFX_setl_gross_limit(null);

            if (!"".equals(gdla))
              newRec.setGold_depo_limit_amt(gdla);
            else
              newRec.setGold_depo_limit_amt(null);

            if (!"".equals(gdgl))
              newRec.setGold_depo_gross_limit(gdgl);
            else
              newRec.setGold_depo_gross_limit(null);

            if (!"".equals(gola))
              newRec.setGold_opt_limit_amt(gola);
            else
              newRec.setGold_opt_limit_amt(null);

            if (!"".equals(gogl))
              newRec.setGold_opt_gross_limit(gogl);
            else
              newRec.setGold_opt_gross_limit(null);

            if (!"".equals(ndla))
              newRec.setNYRO_depo_limit_amt(ndla);
            else
              newRec.setNYRO_depo_limit_amt(null);

            if (!"".equals(ndgl))
              newRec.setNYRO_depo_gross_limit(ndgl);
            else
              newRec.setNYRO_depo_gross_limit(null);

            if (!"".equals(nbla))
              newRec.setNYRO_bond_limit_amt(nbla);
            else
              newRec.setNYRO_bond_limit_amt(null);

            if (!"".equals(nbgl))
              newRec.setNYRO_bond_gross_limit(nbgl);
            else
              newRec.setNYRO_bond_gross_limit(null);

            if (!"".equals(nrla))
              newRec.setNYRO_repo_limit_amt(nrla);
            else
              newRec.setNYRO_repo_limit_amt(null);

            if (!"".equals(nrgl))
              newRec.setNYRO_repo_gross_limit(nrgl);
            else
              newRec.setNYRO_repo_gross_limit(null);

            if (!"".equals(ldla))
              newRec.setLRO_depo_limit_amt(ldla);
            else
              newRec.setLRO_depo_limit_amt(null);

            if (!"".equals(ldgl))
              newRec.setLRO_depo_gross_limit(ldgl);
            else
              newRec.setLRO_depo_gross_limit(null);

            if (!"".equals(lbla))
              newRec.setLRO_bond_limit_amt(lbla);
            else
              newRec.setLRO_bond_limit_amt(null);

            if (!"".equals(lbgl))
              newRec.setLRO_bond_gross_limit(lbgl);
            else
              newRec.setLRO_bond_gross_limit(null);

            if (!"".equals(lrla))
              newRec.setLRO_repo_limit_amt(lrla);
            else
              newRec.setLRO_repo_limit_amt(null);

            if (!"".equals(lrgl))
              newRec.setLRO_repo_gross_limit(lrgl);
            else
              newRec.setLRO_repo_gross_limit(null);

            if (!"".equals(mcr))
              newRec.setMoody_cr_rating(mcr);
            else
              newRec.setMoody_cr_rating("");

            if (!"".equals(fcr))
              newRec.setFitch_cr_rating(fcr);
            else
              newRec.setFitch_cr_rating("");

            if (!"".equals(fsq))
              newRec.setFitch_sort_seq(fsq);
            else
              newRec.setFitch_sort_seq("");

            if (!"".equals(msq))
              newRec.setMoody_sort_seq(msq);
            else
              newRec.setMoody_sort_seq("");
            newRec.setCrt_TMS(tms);
            newRec.setLast_mod_TMS(tms);
            newRec.setCrt_UID(userId);
            newRec.setLast_mod_UID(userId);
            newRec.setLast_upd_pgm("SaveHoldLmtMaintAction");
            hbnSession.save(newRec);
          }else{*/
            if (!"".equals(bla))
              updRec.setBond_limit_amt(bla);
            else
              updRec.setBond_limit_amt(null);

            if (!"".equals(bgl))
              updRec.setBond_gross_limit(bgl);
            else
              updRec.setBond_gross_limit(null);

            if (!"".equals(rla))
              updRec.setRepo_limit_amt(rla);
            else
              updRec.setRepo_limit_amt(null);

            if (!"".equals(rgl))
              updRec.setRepo_gross_limit(rgl);
            else
              updRec.setRepo_gross_limit(null);

            if (!"".equals(dla))
              updRec.setDepo_limit_amt(dla);
            else
              updRec.setDepo_limit_amt(null);

            if (!"".equals(dgl))
              updRec.setDepo_gross_limit(dgl);
            else
              updRec.setDepo_gross_limit(null);

            if (!"".equals(fpla))
              updRec.setFX_presetl_limit_amt(fpla);
            else
              updRec.setFX_presetl_limit_amt(null);

            if (!"".equals(fpgl))
              updRec.setFX_presetl_gross_limit(fpgl);
            else
              updRec.setFX_presetl_gross_limit(null);

            if (!"".equals(fsla))
              updRec.setFX_setl_limit_amt(fsla);
            else
              updRec.setFX_setl_limit_amt(null);

            if (!"".equals(fsgl))
              updRec.setFX_setl_gross_limit(fsgl);
            else
              updRec.setFX_setl_gross_limit(null);

            if (!"".equals(gdla))
              updRec.setGold_depo_limit_amt(gdla);
            else
              updRec.setGold_depo_limit_amt(null);

            if (!"".equals(gdgl))
              updRec.setGold_depo_gross_limit(gdgl);
            else
              updRec.setGold_depo_gross_limit(null);

            if (!"".equals(gola))
              updRec.setGold_opt_limit_amt(gola);
            else
              updRec.setGold_opt_limit_amt(null);

            if (!"".equals(gogl))
              updRec.setGold_opt_gross_limit(gogl);
            else
              updRec.setGold_opt_gross_limit(null);

            if (!"".equals(ndla))
              updRec.setNYRO_depo_limit_amt(ndla);
            else
              updRec.setNYRO_depo_limit_amt(null);

            if (!"".equals(ndgl))
              updRec.setNYRO_depo_gross_limit(ndgl);
            else
              updRec.setNYRO_depo_gross_limit(null);

            if (!"".equals(nbla))
              updRec.setNYRO_bond_limit_amt(nbla);
            else
              updRec.setNYRO_bond_limit_amt(null);

            if (!"".equals(nbgl))
              updRec.setNYRO_bond_gross_limit(nbgl);
            else
              updRec.setNYRO_bond_gross_limit(null);

            if (!"".equals(nrla))
              updRec.setNYRO_repo_limit_amt(nrla);
            else
              updRec.setNYRO_repo_limit_amt(null);

            if (!"".equals(nrgl))
              updRec.setNYRO_repo_gross_limit(nrgl);
            else
              updRec.setNYRO_repo_gross_limit(null);

            if (!"".equals(ldla))
              updRec.setLRO_depo_limit_amt(ldla);
            else
              updRec.setLRO_depo_limit_amt(null);

            if (!"".equals(ldgl))
              updRec.setLRO_depo_gross_limit(ldgl);
            else
              updRec.setLRO_depo_gross_limit(null);

            if (!"".equals(lbla))
              updRec.setLRO_bond_limit_amt(lbla);
            else
              updRec.setLRO_bond_limit_amt(null);

            if (!"".equals(lbgl))
              updRec.setLRO_bond_gross_limit(lbgl);
            else
              updRec.setLRO_bond_gross_limit(null);

            if (!"".equals(lrla))
              updRec.setLRO_repo_limit_amt(lrla);
            else
              updRec.setLRO_repo_limit_amt(null);

            if (!"".equals(lrgl))
              updRec.setLRO_repo_gross_limit(lrgl);
            else
              updRec.setLRO_repo_gross_limit(null);

            if (!"".equals(mcr))
              updRec.setMoody_cr_rating(mcr);
            else
              updRec.setMoody_cr_rating("");

            if (!"".equals(fcr))
              updRec.setFitch_cr_rating(fcr);
            else
              updRec.setFitch_cr_rating("");
            
            if (!"".equals(fsq))
              updRec.setFitch_sort_seq(fsq);
            else
              updRec.setFitch_sort_seq("");

            if (!"".equals(msq))
              updRec.setMoody_sort_seq(msq);
            else
              updRec.setMoody_sort_seq("");

            if (!"".equals(temp_bla))
              updRec.setBond_temp_limit(temp_bla);
            else
              updRec.setBond_temp_limit(null);

            if (!"".equals(temp_bgl))
              updRec.setBond_temp_gross_limit(temp_bgl);
            else
              updRec.setBond_temp_gross_limit(null);

            if (!"".equals(temp_rla))
              updRec.setRepo_temp_limit(temp_rla);
            else
              updRec.setRepo_temp_limit(null);

            if (!"".equals(temp_rgl))
              updRec.setRepo_temp_gross_limit(temp_rgl);
            else
              updRec.setRepo_temp_gross_limit(null);


            if (!"".equals(bond_effdate))
              updRec.setBond_temp_eff_date(bond_effdate);
            else
              updRec.setBond_temp_eff_date(null);

            if (!"".equals(repo_effdate))
              updRec.setRepo_temp_eff_date(repo_effdate);
            else
              updRec.setRepo_temp_eff_date(null);

            if (!"".equals(bond_date))
              updRec.setBond_temp_exp_date(bond_date);
            else
              updRec.setBond_temp_exp_date(null);

            if (!"".equals(repo_date))
              updRec.setRepo_temp_exp_date(repo_date);
            else
              updRec.setRepo_temp_exp_date(null);

            updRec.setLast_mod_TMS(tms);
            updRec.setLast_mod_UID(userId);
            updRec.setLast_upd_pgm("UpdHoldLmtMaintAction");
          //}
        }else{
           errMsg = "Record does not exist or has been modified by other user.";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
        }
        tx.commit();
        HibernateUtil.closeSession();
        PortletSession sess = req.getPortletSession(false);
       } catch (Exception ex) {
         System.out.println("Exception:"+ex.toString());
         if (tx != null) tx.rollback();
           throw ex;
         }
       }
       //-------------------------- End - Limit Amount --> Edit ----------------------------------

       //-------------------------- Exposure % --> Add ----------------------------------
       else if("ExposurePct_ADD".equalsIgnoreCase(mode)){
         Session hbnSession = HibernateUtil.currentSession();
         String sql="";
         String[] ExposurePctData = StringUtil.split(ParamUtil.getString(req, "ExposurePctData"),StringPool.UNDERLINE);

         List lstExpsrData = new ArrayList();
         for(int i =0;i<ExposurePctData.length;i++) {
           String[] strExpsrData = StringUtil.split(ExposurePctData[i],StringPool.AT);
           lstExpsrData.add(strExpsrData);
         } 
         //---Check mandatory & integrity
         for (int i=0; i<lstExpsrData.size(); i++){
           String[]  strExpsrData = (String[]) lstExpsrData.get(i);
           if (!chkIntegrity(strExpsrData)){
             res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
             return;
           }
         }
         DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
         Transaction tx = hbnSession.beginTransaction();
         try{
           for (int i=0; i<lstExpsrData.size(); i++){
             String[] strExpsrData = (String[]) lstExpsrData.get(i);
             String shortDate = strExpsrData[2].substring(0,4)+strExpsrData[2].substring(5,7)+strExpsrData[2].substring(8,10);
             //---check whether record exist or not
             List leafRec = hbnSession.createSQLQuery("SELECT Data_leaf_ID FROM IDDDataLeafProf " + 
                         "WHERE Data_leaf_desc = '"+strExpsrData[1]+"'")
                         .addScalar("Data_leaf_ID", Hibernate.STRING)
                         .list();
             Object target = (Object) leafRec.get(0);
             String targetString = target.toString().trim();
             List brchRec = hbnSession.createSQLQuery("SELECT Brch FROM IDDTERASBrchProf WHERE Brch_name = '"+strExpsrData[0]+"'")
                           .addScalar("Brch", Hibernate.STRING)
                           .list();
             Object brchtemp = (Object) brchRec.get(0);
             String targetBrch = brchtemp.toString().trim();

             Query query = hbnSession.createQuery("select x from IDDExpsrPct as x where convert(char(8),Eff_start_date,112)='"+shortDate+"' "+
             "and Brch_code ='"+targetBrch+"' and Data_leaf = '"+targetString+"'");

             List lstRec = query.list();
             if (lstRec.size()==0){
               //---Insert into tbl
               IDDExpsrPct Newrec = new IDDExpsrPct();
               Newrec.setEff_start_date(strExpsrData[2] +" 00:00:00.0");
               Newrec.setBrch_code(targetBrch);
               Newrec.setData_leaf(targetString);
               Newrec.setLeaf_desc(strExpsrData[1]);
               if (!"NULL".equals(strExpsrData[3]))
                 Newrec.setHldg_pct(strExpsrData[3]);
               else
                 Newrec.setHldg_pct(null);
               //Newrec.setDel_flag("N");
               Newrec.setCrt_TMS(getTMS(2));
               Newrec.setLast_mod_TMS(getTMS(1));
               Newrec.setCrt_UID(userId);
               Newrec.setLast_mod_UID(userId);
               Newrec.setLast_upd_pgm("SaveExpsrPctAction");
               hbnSession.save(Newrec);
             }else{
               HibernateUtil.closeSession();
               errMsg = "Record already exist (Branch: "+strExpsrData[0]+" Data leaf description: "+strExpsrData[1]+" Effective date: "+strExpsrData[2]+" " +
               ").";
               res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
               return;
             }
           }
           tx.commit();
           HibernateUtil.closeSession();
           PortletSession sess = req.getPortletSession(false);
         } catch (Exception ex) {
           System.out.println("Exception:"+ex.toString());
           if (tx != null) tx.rollback();
             throw ex;
         }
       }
       //-------------------------- End - Exposure % --> Add ----------------------------------

       //-------------------------- Exposure % --> Edit ----------------------------------
       else if("ExposurePct_EDIT".equalsIgnoreCase(mode)){
         Session hbnSession = HibernateUtil.currentSession();
         String BrchName = ParamUtil.getString(req, "Brch_name")==null?"":ParamUtil.getString(req, "Brch_name");
         String LeafDesc = ParamUtil.getString(req, "Leaf_desc")==null?"":ParamUtil.getString(req, "Leaf_desc");
         String Eff_date = ParamUtil.getString(req, "Eff_date")==null?"":ParamUtil.getString(req, "Eff_date");
         String Hldg_pct = ParamUtil.getString(req, "Hldg_pct1")==null?"":ParamUtil.getString(req, "Hldg_pct1");
         String New_eff_date = ParamUtil.getString(req, "neweffdate")==null?"":ParamUtil.getString(req, "neweffdate");
         String Last_mod_TMS = ParamUtil.getString(req, "Last_mod_TMS")==null?"":ParamUtil.getString(req, "Last_mod_TMS");

         String ValidNewDate ="";
         //---check mandatory & integrity
         if (!validate.isValidShortDate(Eff_date)){
           errMsg = "Invalid Effective Date Format ("+Eff_date+")";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
         }
         if(!New_eff_date.equals("NULL")){
           ValidNewDate = New_eff_date.substring(0,4)+"-"+New_eff_date.substring(4,6)+"-"+New_eff_date.substring(6,8);
           if (!validate.isValidShortDate(ValidNewDate)){
             errMsg = "Invalid Effective Date Format ("+ValidNewDate+")";
             res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
             return;
           }
         }
         if(!validate.isValidTimestamp(Last_mod_TMS)) {
           errMsg = "Invalid timestamp format ("+Last_mod_TMS+")";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
         }
         if (!validate.isValidCharacter(Hldg_pct,"1234567890. ")){
           errMsg = "Invalid Holding % ("+Hldg_pct+")";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
         }
         if (BrchName.equals("")){
           errMsg = "Invalid Branch Name ("+BrchName+")";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
         }
         if (LeafDesc.equals("")){
           errMsg = "Invalid Data Leaf Description ("+LeafDesc+")";
           res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
           return;
         }
         Transaction tx = hbnSession.beginTransaction();
         try{
           List leafRec = hbnSession.createSQLQuery("SELECT Data_leaf_ID FROM IDDDataLeafProf " + 
                          "WHERE Data_leaf_desc = '"+LeafDesc+"'")
                          .addScalar("Data_leaf_ID", Hibernate.STRING)
                          .list();
           String Leaf_ID="";
           if (leafRec.size()>0){
             Object target = (Object) leafRec.get(0);
             Leaf_ID = target.toString().trim();
           }else{
             Leaf_ID = LeafDesc;
           }

           List brchRec = hbnSession.createSQLQuery("SELECT Brch FROM IDDTERASBrchProf WHERE Brch_name = '"+BrchName+"'")
                         .addScalar("Brch", Hibernate.STRING)
                         .list();
           String Brch_code="00";
           if (brchRec.size()>0){
             Object brchtemp = (Object) brchRec.get(0);
             Brch_code = brchtemp.toString().trim();
           }
           String dt = Eff_date.substring(0,4)+Eff_date.substring(5,7)+Eff_date.substring(8,10);
           Query query = hbnSession.createQuery("select x from IDDExpsrPct as x where Brch_code = '"+Brch_code+"' and Data_leaf = '"+Leaf_ID+"' and convert(char(8),Eff_start_date,112)='"+dt+"' and Last_mod_TMS='"+Last_mod_TMS+"'");
           List lstRec = query.list();
           System.out.println("---lstRec size:"+lstRec.size());

           //---check record whether been modified
           if (lstRec.size()>0){
             IDDExpsrPct updRec = (IDDExpsrPct)lstRec.get(0);
             //---check whether new date issued
             if(New_eff_date.equalsIgnoreCase("NULL")){
               //---update starting...
               updRec.setBrch_code(Brch_code);
               updRec.setData_leaf(Leaf_ID);
               updRec.setLeaf_desc(LeafDesc);
               //updRec.setEff_start_date(Eff_date+" 00:00:00.0");
               if("".equals(Hldg_pct))
                 updRec.setHldg_pct(null);
               else
                 updRec.setHldg_pct(Hldg_pct);
               updRec.setLast_mod_TMS(getTMS(1));
               updRec.setLast_mod_UID(userId);
               updRec.setLast_upd_pgm("UpdExpsrPctAction");
             }else{
               IDDExpsrPct Newrec = new IDDExpsrPct();
               Newrec.setBrch_code(Brch_code);
               Newrec.setData_leaf(Leaf_ID);
               Newrec.setLeaf_desc(LeafDesc);
               Newrec.setEff_start_date(ValidNewDate+" 00:00:00.0");
               Newrec.setEff_end_date(null);
               if("".equals(Hldg_pct))
                 Newrec.setHldg_pct(null);
               else
                 Newrec.setHldg_pct(Hldg_pct);
               //Newrec.setDel_flag("N");
               Newrec.setLast_mod_TMS(getTMS(1));
               Newrec.setLast_mod_UID(userId);
               Newrec.setLast_upd_pgm("SaveExpsrPctAction");
               Newrec.setCrt_TMS(getTMS(2));
               Newrec.setCrt_UID(userId);
               updRec.setLast_mod_TMS(getTMS(1));
               updRec.setLast_mod_UID(userId);
               updRec.setLast_upd_pgm("UpdExpsrPctAction");

               DateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
               Date yesterdayDate= df.parse(ValidNewDate);
               yesterdayDate.setDate(yesterdayDate.getDate()-1);
               String syesterdayDate= df.format(yesterdayDate);
               updRec.setEff_end_date(syesterdayDate + " 00:00:00.0");
               hbnSession.save(Newrec);
             }
           }else{
             errMsg = "Record does not exist or has been modified by other user.";
             res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
             return;
           }
           tx.commit();
           HibernateUtil.closeSession();
           PortletSession sess = req.getPortletSession(false);
         }catch (Exception ex) {
           if (tx != null) tx.rollback();
             throw ex;
         }
       }
       //-------------------------- End - Exposure % --> Edit ----------------------------------
       //-------------------------- Limit % --> Edit ----------------------------------
    if ("LmtPct_EDIT".equalsIgnoreCase(mode)){
      Session hbnSession = HibernateUtil.currentSession();
      String sql="";
      String Eff_date = ParamUtil.getString(req, "Eff_date")==null?"":ParamUtil.getString(req, "Eff_date");
      String NewEffDate = ParamUtil.getString(req, "NewEffDate")==null?"":ParamUtil.getString(req, "NewEffDate");
      String Brch_code = ParamUtil.getString(req, "BrchCode")==null?"":ParamUtil.getString(req, "BrchCode");
      String Grp_ID = ParamUtil.getString(req, "Group_ID")==null?"":ParamUtil.getString(req, "Group_ID");
      String Swap_limit = ParamUtil.getString(req, "Swap_limit")==null?"":ParamUtil.getString(req, "Swap_limit");
      String Last_mod_TMS = ParamUtil.getString(req, "Last_mod_TMS")==null?"":ParamUtil.getString(req, "Last_mod_TMS");
      
      //---check mandatory & integrity
      if (!validate.isValidShortDate(Eff_date)){
        errMsg = "Invalid Effective Date Format ("+Eff_date+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }
      if (!validate.isValidShortDate(NewEffDate)&&!"NULL".equals(NewEffDate)){
        errMsg = "Invalid New Effective Date Format ("+NewEffDate+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }
      if(!validate.isValidTimestamp(Last_mod_TMS)) {
        errMsg = "Invalid timestamp format ("+Last_mod_TMS+")";
        res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
        return;
      }
      
      //---check record whether been modified
      Transaction tx = hbnSession.beginTransaction();
      DateFormat dateformat= new SimpleDateFormat("yyyy-MM-dd");
      try{
        Query query = hbnSession.createQuery("select x from IDDHldgPct as x where convert(char(10),x.primaryKey.Eff_start_date,120)='"+Eff_date+"' and Group_ID='"+Grp_ID+"' and Last_mod_TMS = '"+Last_mod_TMS+"'");
        List lstRec = query.list();
        String tms = getTMS(1);
        if (lstRec.size()>0){
          IDDHldgPct updRec = (IDDHldgPct)lstRec.get(0);
          if (!"NULL".equals(NewEffDate)){
          	//---Update & Insert
            Date dt = dateformat.parse(NewEffDate);
            dt.setDate(dt.getDate()-1);
            String strDate = dateformat.format(dt);
            updRec.setEff_end_date(strDate+" 00:00:00.0");
            
            IDDHldgPct newRec = new IDDHldgPct();
            newRec.setBrch_code(Brch_code);
            newRec.setGroup_ID(Grp_ID);
            newRec.setSwap_limit(Swap_limit);
            newRec.setEff_start_date(NewEffDate+" 00:00:00.0");
            newRec.setEff_end_date(null);            
            newRec.setCrt_TMS(tms);
            newRec.setLast_mod_TMS(tms);
            newRec.setCrt_UID(userId);
            newRec.setLast_mod_UID(userId);
            newRec.setLast_upd_pgm("SaveHoldLmtMaintAction");
            hbnSession.save(newRec);
          }
          else{
            updRec.setSwap_limit(Swap_limit);
          }
          updRec.setLast_mod_TMS(tms);
          updRec.setLast_mod_UID(userId);
          updRec.setLast_upd_pgm("UpdHoldLmtMaintAction");
        }else{
          errMsg = "Record does not exist or has been modified by other user.";
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName);
          return;
        }
        tx.commit();
        HibernateUtil.closeSession();
        PortletSession sess = req.getPortletSession(false);
        }catch (Exception ex) {
          if (tx != null) tx.rollback();
            throw ex;
         }
    }
    //--------------------------End - Limit % --> Edit ----------------------------------
    //-------------------------- Limit % --> Add ---------------------------------------- 
    else if ("LmtPct_ADD".equalsIgnoreCase(mode)){
      Session hbnSession = HibernateUtil.currentSession();
      String sql="";
      String[] LmtPctData = StringUtil.split(ParamUtil.getString(req, "LmtPctData"),StringPool.UNDERLINE);
      
      
      List lstLmtPctData = new ArrayList();
      for(int i =0;i<LmtPctData.length;i++) {
        String[] strLmtPctData = StringUtil.split(LmtPctData[i],StringPool.AT);
        lstLmtPctData.add(strLmtPctData);
      }
      
    //---Check mandatory & integrity
      for (int i=0; i<lstLmtPctData.size(); i++){
        String[]  strLmtPctData = (String[]) lstLmtPctData.get(i);
        if (!chkIntegrity(strLmtPctData)){
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
          return;
        }
      }
      DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
      Transaction tx = hbnSession.beginTransaction();
      
      try{
        for (int i=0; i<lstLmtPctData.size(); i++){
          String[] strLmtPctData = (String[]) lstLmtPctData.get(i);
          //---check whether record exist or not
          List brchRec = hbnSession.createSQLQuery("SELECT DISTINCT Brch FROM IDDTERASBrchProf WHERE Brch = '"+strLmtPctData[0]+"'")
                         .addScalar("Brch", Hibernate.STRING)
                         .list();
          Object target = (Object) brchRec.get(0);
          String targetBrch = target.toString().trim();
          List grpRec = hbnSession.createSQLQuery("Select Cust_name from IDDTERASCustProf WHERE Cust_name = '"+strLmtPctData[1]+"'")
                        .addScalar("Cust_name", Hibernate.STRING)
                        .list();
          Object target1 = (Object) grpRec.get(0);
          String targetGrp = target1.toString().trim();
          
          Query query = hbnSession.createQuery("select x from IDDHldgPct as x where Brch_code ='"+targetBrch+"' and Group_ID='"+targetGrp+"'");
          
          List lstRec = query.list();
          if (lstRec.size()==0){
          //---Insert into tbl
           IDDHldgPct Newrec = new IDDHldgPct();
           Newrec.setEff_start_date(strLmtPctData[2] +" 00:00:00.0");
           Newrec.setBrch_code(targetBrch);
           Newrec.setGroup_ID(targetGrp);
           Newrec.setSwap_limit(strLmtPctData[3]);
           Newrec.setEff_end_date(null);
           Newrec.setCrt_TMS(getTMS(1));
           Newrec.setLast_mod_TMS(getTMS(1));
           Newrec.setCrt_UID(userId);
           Newrec.setLast_mod_UID(userId);
           Newrec.setLast_upd_pgm("SaveHoldLmtMaintAction");
           hbnSession.save(Newrec);
          }else if (lstRec.size()>0) {
           //select max eff_start_date
           Query query1 = hbnSession.createQuery("select x from IDDHldgPct as x where Eff_end_date is null and Brch_code ='"+targetBrch+"' and Group_ID='"+targetGrp+"'");
           //sql = "Select max(eff_start_date) as max,Last_mod_TMS,Last_mod_UID,Eff_end_date from IDDHldgPct where eff_end_date is null and Brch_code ='"+targetBrch+"' and Group_ID='"+targetGrp+"' Group by Last_mod_TMS,Last_mod_UID,Eff_end_date ";
           System.out.println("sql "+sql);
           List lstEffDt = query1.list();
           System.out.println("query1 "+query1);
           /*List lstEffDt = hbnSession.createSQLQuery(sql)
                                     .addScalar("max",Hibernate.STRING)
                                     .list();
           */
           if (lstEffDt.size()==1){
             DateFormat effdtFormat= new SimpleDateFormat("yyyy-MM-dd");
             System.out.println("Passing 1");

             Date dtMax = new Date();
             dtMax = effdtFormat.parse(strLmtPctData[2]);
             dtMax.setDate(dtMax.getDate()-1);
             String strDate = effdtFormat.format(dtMax);
             System.out.println("dtMax: "+dtMax);
             System.out.println("strDate: "+strDate);

             IDDHldgPct updRec = (IDDHldgPct)lstEffDt.get(0);
             System.out.println("1");
             updRec.setEff_end_date(strDate);
             System.out.println("2");
             updRec.setLast_mod_TMS(getTMS(1));
             System.out.println("3");
             updRec.setLast_upd_pgm("UpdateHoldLmtMaintAction");
             
             System.out.println("Passing 2");
             IDDHldgPct Rec = new IDDHldgPct();
             Rec.setEff_start_date(strLmtPctData[2] +" 00:00:00.0");
             Rec.setBrch_code(targetBrch);
             Rec.setGroup_ID(targetGrp);
             Rec.setSwap_limit(strLmtPctData[3]);
             Rec.setCrt_TMS(getTMS(1));
             Rec.setLast_mod_TMS(getTMS(1));
             Rec.setCrt_UID(userId);
             Rec.setLast_mod_UID(userId);
             Rec.setLast_upd_pgm("SaveHoldLmtMaintAction");
             hbnSession.save(Rec);
             System.out.println("Passing 3");
           }
         }
        }
        tx.commit();
        HibernateUtil.closeSession();
        PortletSession sess = req.getPortletSession(false);
        }catch (Exception ex) {
          if (tx != null) tx.rollback();
            throw ex;
         }    
    }
    //-------------------------- End - Limit % --> Add ----------------------------------------   
    //-------------------------- Limit Amount --> Add ----------------------------------------
   else if ("LmtAmt_ADD".equalsIgnoreCase(mode)){
      Session hbnSession = HibernateUtil.currentSession();
      String sql="";
      String[] LmtAmtData = StringUtil.split(ParamUtil.getString(req, "LmtAmtData"),StringPool.UNDERLINE);
      String tms = getTMS(1);
      List lstLmtAmtData = new ArrayList();
      for(int i =0;i<LmtAmtData.length;i++) {
        String[] strLmtAmtData = StringUtil.split(LmtAmtData[i],StringPool.AT);
        lstLmtAmtData.add(strLmtAmtData);
      }
      System.out.println("lstLmtAmtData.size() "+lstLmtAmtData.size());
    //---Check mandatory & integrity
      /*for (int i=0; i<lstLmtAmtData.size(); i++){
        String[]  strLmtAmtData = (String[]) lstLmtAmtData.get(i);
        if (!chkIntegrity(strLmtAmtData)){
          res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
          return;
        }
      }*/

      Transaction tx = hbnSession.beginTransaction();
      
      try{
        for (int i=0; i<lstLmtAmtData.size(); i++){
          String[] strLmtAmtData = (String[]) lstLmtAmtData.get(i);
          
          Query query = hbnSession.createQuery("select x from IDDTERASCPLimit as x where Grp_ID='"+strLmtAmtData[0]+"'");
          
          List lstRec = query.list();
          System.out.println("lstRec.size() "+lstRec.size());
          System.out.println("LmtAmtData.length "+LmtAmtData.length);
          /*for (int t=0;t<31;t++){
            System.out.println("strLmtAmtData["+t+"] "+strLmtAmtData[t]);	
          }*/

          if (lstRec.size()==0){
          //---Insert into tbl
           IDDTERASCPLimit newRec = new IDDTERASCPLimit();
            //newRec.setDate(strLmtAmtData[1]+" 00:00:00.0");
            newRec.setGrp_ID(strLmtAmtData[0]);
            System.out.println("strLmtAmtData[0] "+strLmtAmtData[0]);
            System.out.println("strLmtAmtData[1] "+strLmtAmtData[1]);
            if (!"".equals(strLmtAmtData[2]))
              newRec.setBond_limit_amt(strLmtAmtData[2]);
            else
              newRec.setBond_limit_amt(null);
            System.out.println("strLmtAmtData[2] "+strLmtAmtData[2]);
            if (!"".equals(strLmtAmtData[3]))
              newRec.setBond_gross_limit(strLmtAmtData[3]);
            else
              newRec.setBond_gross_limit(null);
            System.out.println("strLmtAmtData[3] "+strLmtAmtData[3]);

            if (!"".equals(strLmtAmtData[4]))
              newRec.setRepo_limit_amt(strLmtAmtData[4]);
            else
              newRec.setRepo_limit_amt(null);
            System.out.println("strLmtAmtData[4] "+strLmtAmtData[4]);

            if (!"".equals(strLmtAmtData[5]))
              newRec.setRepo_gross_limit(strLmtAmtData[5]);
            else
              newRec.setRepo_gross_limit(null);
            System.out.println("strLmtAmtData[5] "+strLmtAmtData[5]);

            if (!"".equals(strLmtAmtData[6]))
              newRec.setDepo_limit_amt(strLmtAmtData[6]);
            else
              newRec.setDepo_limit_amt(null);
            System.out.println("strLmtAmtData[6] "+strLmtAmtData[6]);

            if (!"".equals(strLmtAmtData[7]))
              newRec.setDepo_gross_limit(strLmtAmtData[7]);
            else
              newRec.setDepo_gross_limit(null);
            System.out.println("strLmtAmtData[7] "+strLmtAmtData[7]);

            if (!"".equals(strLmtAmtData[8]))
              newRec.setFX_presetl_limit_amt(strLmtAmtData[8]);
            else
              newRec.setFX_presetl_limit_amt(null);
            System.out.println("strLmtAmtData[8] "+strLmtAmtData[8]);

            if (!"".equals(strLmtAmtData[9]))
              newRec.setFX_presetl_gross_limit(strLmtAmtData[9]);
            else
              newRec.setFX_presetl_gross_limit(null);
            System.out.println("strLmtAmtData[9] "+strLmtAmtData[9]);

            if (!"".equals(strLmtAmtData[10]))
              newRec.setFX_setl_limit_amt(strLmtAmtData[10]);
            else
              newRec.setFX_setl_limit_amt(null);
            System.out.println("strLmtAmtData[10] "+strLmtAmtData[10]);

            if (!"".equals(strLmtAmtData[11]))
              newRec.setFX_setl_gross_limit(strLmtAmtData[11]);
            else
              newRec.setFX_setl_gross_limit(null);
            System.out.println("strLmtAmtData[11] "+strLmtAmtData[11]);

            if (!"".equals(strLmtAmtData[12]))
              newRec.setGold_depo_limit_amt(strLmtAmtData[12]);
            else
              newRec.setGold_depo_limit_amt(null);
            System.out.println("strLmtAmtData[12] "+strLmtAmtData[12]);

            if (!"".equals(strLmtAmtData[13]))
              newRec.setGold_depo_gross_limit(strLmtAmtData[13]);
            else
              newRec.setGold_depo_gross_limit(null);
            System.out.println("strLmtAmtData[13] "+strLmtAmtData[13]);

            if (!"".equals(strLmtAmtData[14]))
              newRec.setGold_opt_limit_amt(strLmtAmtData[14]);
            else
              newRec.setGold_opt_limit_amt(null);
            System.out.println("strLmtAmtData[14] "+strLmtAmtData[14]);

            if (!"".equals(strLmtAmtData[15]))
              newRec.setGold_opt_gross_limit(strLmtAmtData[15]);
            else
              newRec.setGold_opt_gross_limit(null);
            System.out.println("strLmtAmtData[15] "+strLmtAmtData[15]);

            if (!"".equals(strLmtAmtData[16]))
              newRec.setNYRO_depo_limit_amt(strLmtAmtData[16]);
            else
              newRec.setNYRO_depo_limit_amt(null);
            System.out.println("strLmtAmtData[16] "+strLmtAmtData[16]);

            if (!"".equals(strLmtAmtData[17]))
              newRec.setNYRO_depo_gross_limit(strLmtAmtData[17]);
            else
              newRec.setNYRO_depo_gross_limit(null);
            System.out.println("strLmtAmtData[17] "+strLmtAmtData[17]);

            if (!"".equals(strLmtAmtData[18]))
              newRec.setNYRO_bond_limit_amt(strLmtAmtData[18]);
            else
              newRec.setNYRO_bond_limit_amt(null);
            System.out.println("strLmtAmtData[18] "+strLmtAmtData[18]);

            if (!"".equals(strLmtAmtData[19]))
              newRec.setNYRO_bond_gross_limit(strLmtAmtData[19]);
            else
              newRec.setNYRO_bond_gross_limit(null);
            System.out.println("strLmtAmtData[19] "+strLmtAmtData[19]);

            if (!"".equals(strLmtAmtData[20]))
              newRec.setNYRO_repo_limit_amt(strLmtAmtData[20]);
            else
              newRec.setNYRO_repo_limit_amt(null);
            System.out.println("strLmtAmtData[20] "+strLmtAmtData[20]);

            if (!"".equals(strLmtAmtData[21]))
              newRec.setNYRO_repo_gross_limit(strLmtAmtData[21]);
            else
              newRec.setNYRO_repo_gross_limit(null);
            System.out.println("strLmtAmtData[21] "+strLmtAmtData[21]);

            if (!"".equals(strLmtAmtData[22]))
              newRec.setLRO_depo_limit_amt(strLmtAmtData[22]);
            else
              newRec.setLRO_depo_limit_amt(null);
            System.out.println("strLmtAmtData[22] "+strLmtAmtData[22]);

            if (!"".equals(strLmtAmtData[23]))
              newRec.setLRO_depo_gross_limit(strLmtAmtData[23]);
            else
              newRec.setLRO_depo_gross_limit(null);
            System.out.println("strLmtAmtData[23] "+strLmtAmtData[23]);

            if (!"".equals(strLmtAmtData[24]))
              newRec.setLRO_bond_limit_amt(strLmtAmtData[24]);
            else
              newRec.setLRO_bond_limit_amt(null);
            System.out.println("strLmtAmtData[24] "+strLmtAmtData[24]);

            if (!"".equals(strLmtAmtData[25]))
              newRec.setLRO_bond_gross_limit(strLmtAmtData[25]);
            else
              newRec.setLRO_bond_gross_limit(null);
            System.out.println("strLmtAmtData[25] "+strLmtAmtData[25]);

            if (!"".equals(strLmtAmtData[26]))
              newRec.setLRO_repo_limit_amt(strLmtAmtData[26]);
            else
              newRec.setLRO_repo_limit_amt(null);
            System.out.println("strLmtAmtData[26] "+strLmtAmtData[26]);

            if (!"".equals(strLmtAmtData[27]))
              newRec.setLRO_repo_gross_limit(strLmtAmtData[27]);
            else
              newRec.setLRO_repo_gross_limit(null);
            System.out.println("strLmtAmtData[27] "+strLmtAmtData[27]);

            if (!"".equals(strLmtAmtData[28]))
              newRec.setMoody_cr_rating(strLmtAmtData[28]);
            else
              newRec.setMoody_cr_rating("");
            System.out.println("strLmtAmtData[28] "+strLmtAmtData[28]);

            if (!"".equals(strLmtAmtData[29]))
              newRec.setFitch_cr_rating(strLmtAmtData[29]);
            else
              newRec.setFitch_cr_rating("");
            System.out.println("strLmtAmtData[29] "+strLmtAmtData[29]);

            if (!"".equals(strLmtAmtData[30]))
              newRec.setFitch_sort_seq(strLmtAmtData[30]);
            else
              newRec.setFitch_sort_seq("");
            System.out.println("strLmtAmtData[30] "+strLmtAmtData[30]);

            if (!"".equals(strLmtAmtData[31]))
              newRec.setMoody_sort_seq(strLmtAmtData[31]);
            else
              newRec.setMoody_sort_seq("");
            System.out.println("strLmtAmtData[31] "+strLmtAmtData[31]);

            if (!"".equals(strLmtAmtData[32]))
              newRec.setBond_temp_limit(strLmtAmtData[32]);
            else
              newRec.setBond_temp_limit(null);
            System.out.println("strLmtAmtData[32] "+strLmtAmtData[32]);
            if (!"".equals(strLmtAmtData[33]))
              newRec.setBond_temp_gross_limit(strLmtAmtData[33]);
            else
              newRec.setBond_temp_gross_limit(null);
            System.out.println("strLmtAmtData[33] "+strLmtAmtData[33]);

            if (!"".equals(strLmtAmtData[34]))
              newRec.setRepo_temp_limit(strLmtAmtData[34]);
            else
              newRec.setRepo_temp_limit(null);
            System.out.println("strLmtAmtData[34] "+strLmtAmtData[34]);

            if (!"".equals(strLmtAmtData[35]))
              newRec.setRepo_temp_gross_limit(strLmtAmtData[35]);
            else
              newRec.setRepo_temp_gross_limit(null);
            System.out.println("strLmtAmtData[35] "+strLmtAmtData[35]);

            if (!"".equals(strLmtAmtData[36]))
              newRec.setBond_temp_eff_date(strLmtAmtData[36]);
            else
              newRec.setBond_temp_eff_date(null);
            System.out.println("strLmtAmtData[36] "+strLmtAmtData[36]);

            if (!"".equals(strLmtAmtData[37]))
              newRec.setRepo_temp_eff_date(strLmtAmtData[37]);
            else
              newRec.setRepo_temp_eff_date(null);
            System.out.println("strLmtAmtData[37] "+strLmtAmtData[37]);

            if (!"".equals(strLmtAmtData[38]))
              newRec.setBond_temp_exp_date(strLmtAmtData[38]);
            else
              newRec.setBond_temp_exp_date(null);
            System.out.println("strLmtAmtData[38] "+strLmtAmtData[38]);

            if (!"".equals(strLmtAmtData[39]))
              newRec.setRepo_temp_exp_date(strLmtAmtData[39]);
            else
              newRec.setRepo_temp_exp_date(null);
            System.out.println("strLmtAmtData[39] "+strLmtAmtData[39]);

            newRec.setCrt_TMS(tms);
            newRec.setLast_mod_TMS(tms);
            newRec.setCrt_UID(userId);
            newRec.setLast_mod_UID(userId);
            newRec.setLast_upd_pgm("SaveHoldLmtMaintAction");
            hbnSession.save(newRec);
          }else{
               HibernateUtil.closeSession();
               errMsg = "Record already exist (Group ID: "+strLmtAmtData[0]+" Effective date: "+strLmtAmtData[1]+" " +
               ").";
               res.sendRedirect("/iddm/html/portlet/iddm/common/frmError.jsp?ex="+errMsg+"&portletName="+portletName+"&taskName="+taskName2);
               return;
          }          
        }
        tx.commit();
        HibernateUtil.closeSession();
        PortletSession sess = req.getPortletSession(false);
        }catch (Exception ex) {
          if (tx != null) tx.rollback();
            throw ex;
         }    
    }  
    //-------------------------- End - Limit Amount --> Add ----------------------------------------  
  }

  private boolean chkIntegrity(String[] strExpsrData){
    Validator validate = new Validator();
    if (!validate.isValidShortDate(strExpsrData[2])){
      ErrMsg = "Invalid Date Format ("+strExpsrData[2]+")";
      return false;
    }
    if (strExpsrData[0].equals("")){
      ErrMsg = "Invalid Branch Name ("+strExpsrData[0]+")";
      return false;
    }
    if (strExpsrData[1].equals("")){
      ErrMsg = "Invalid Data Leaf Description ("+strExpsrData[1]+")";
      return false;
    }
    if (!validate.isValidCharacter(strExpsrData[3],"1234567890. ")&&!strExpsrData[3].equals("NULL")){
      ErrMsg = "Invalid Holding % ("+strExpsrData[3]+")";
      return false;
    }           
    return true;
  }

  private String getTMS(int x){
  java.util.Date d = new java.util.Date();
  String fmt = "yyyyMMdd";
  if (x==1) fmt = "yyyy-MM-dd HH:mm:ss.SSS";
  else if (x==2) fmt = "yyyy-MM-dd HH:mm:ss";
  else if (x==3) fmt = "yyyy-MM-dd";
  java.text.SimpleDateFormat sd = new java.text.SimpleDateFormat(fmt);
  return sd.format(d);
  }
  
  public ActionForward render(
    ActionMapping mapping, ActionForm form, PortletConfig config,
      RenderRequest req, RenderResponse res)
      throws Exception {
        System.out.println("-------------------passing 2-----------------");
        if("LmtAmt_EDIT".equalsIgnoreCase(ParamUtil.getString(req, "mode"))||"LmtAmt_ADD".equalsIgnoreCase(ParamUtil.getString(req, "mode")))
          return mapping.findForward("portlet.iddm.lmtamt");
        else if("ExposurePct_EDIT".equalsIgnoreCase(ParamUtil.getString(req, "mode"))||"ExposurePct_ADD".equalsIgnoreCase(ParamUtil.getString(req, "mode")))
          return mapping.findForward("portlet.iddm.exposurepct");
        else if("LmtPct_EDIT".equalsIgnoreCase(ParamUtil.getString(req, "mode"))||"LmtPct_ADD".equalsIgnoreCase(ParamUtil.getString(req, "mode")))
         return mapping.findForward("portlet.iddm.lmtpct");
        else
         return mapping.findForward("portlet.iddm.lmtamt");
     }
}