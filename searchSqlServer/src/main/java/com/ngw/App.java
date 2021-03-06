package com.ngw;

import org.nlpcn.es4sql.Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableSwagger2
public class App {
    public static void main(String[] args) {
//        try {
//            String result = Util.sqlToEsQuery("select CJR_DH,ISFINGERREPEAT,FINGERREPEATNO,TASK_SOURCE,RECEIVE_TIME,ISRETURN,RETURN_TIME,ANNEX,SCHEDULE,APPROVAL,GATHER_CATEGORY,AUDITOR,AUDITEDTIME,ISREGATHER,GATHER_FINGER_MODE,CASE_NAME,CASE_REASON,CITY_CODE,SID,BLOW_CODE,BLOW_STREET,BLOW_DETAIL,BLOW_LONGITUDE,BLOW_LATITUDE,BLOW_EASTWEST,BLOW_NORTHSOUTH,SEQ,CARDID,VALID_DATE,ARRIVE_LOCAL_DATE,LEAVE_LOCAL_DATE,DB_SOURCE,DB_SOURCE_DIS,JOB_DES,IS_XJSSMZ,PASSPORT_NUM,COUNTRY_CODE,FOREIGN_NAME,PASSPORT_VALID_DATE,VISA_PLACE,PASSPORT_TYPE,VISA_DATE,ASSIST_LEVEL,ASSIST_BONUS,ASSIST_PURPOSE,ASSIST_REF_PERSON,ASSIST_REF_CASE,ASSIST_VALID_DATE,ASSIST_EXPLAIN,ASSIST_DEPT_CODE,ASSIST_DEPT_NAME,ASSIST_DATE,ASSIST_CONTACTS,ASSIST_NUMBER,ASSIST_APPROVAL,ASSIST_SIGN,CONTRCAPTURE_CODE,DETAIN_FLAG,REUSE_STATUS,OLD_PERSONID,YRYBH,OTHERSPECIALTY,SPSFTG,CJLCXX,REV1,REV2,REV3,REV4,REV5,REV6,REV7,REV8,REV9,REV10,DEPRTMAC,PUSH_STATUS,PUSH_DATE,REMARK,CJR_XM,CJR_SFHM,CJDW_GAJGJGDM,CJDW_GAJGJGDM_DQ,CJDW_DWMC,CJSJ,CJCSLXDM,SBCSDM,CJSBBH,CJSBRJBBH,CREATE_USER,CREATE_DATETIME,UPDATE_USER,UPDATE_DATETIME,SECRECY,DELETEFLAG,SYSFLAG,RYJCXXCJBH,BCJRYLBDM,JZRYBH,ASJRYBH,AJBH,RYDNABH,RYZWBH,XM,XMHYPY,GMSFHM,CYZJDM,ZJHM,XBDM,CSRQ,NL,JGDM,CYM,CYMHYPY,WWXM,BMCH,GJDM,MZDM,HJD_XZQHDM,HJD_DZMC,HJD_XZJDSQ,XZD_XZQHDM,XZD_DZMC,XZD_XZJDSQ,CSD_XZQHDM,CSD_DZMC,CSD_XZJDSQ,ZZMMDM,HYZKDM,ZJXYDM,XLDM,GRSFDM,TSSFDM,GZDW,ZYDM,LXFS,TCDM,QKUBS,FUGITIVEFLAG,PHONE_NUM,SJHM1,SJHM2,SENDIP,CASE_KIND,HITSTATUS,NY_REASON,NY_REASON33,NY_POLICEID,BASICFLAG,RPFLAG,PALMSTATUS,PHFLAG,DNAFLAG,GXRFLAG,BSFLAG,GDSFLAG,HMFLAG,SWFLAG,SIMMESSAGEFLAG,SIMFLAG,BJFLAG,NJFLAG,ZJFLAG,PALMFLAG,CJDD,FWCS,ISFC,TEMPRYBH,AUTOGEN_IDCARD_FLAG,TONE_CODE,TONE,SOURCEINCOME_CODE,HAVEEMPLOYMENT,HEADSHIP,JOB_ADDRESS,SPECIALIDENTITY_CODE,ISTRANSIENTPOP,ISTEMPREGIST,HAVEPERMIT,HAVERESIDENCE,ISSERVICE,SPECIALGROUP_CODE,HAVESEPARATION,ISMIGRANTWORKER,NAMEOFSCHOOL,ISTRAINING,HAVECERTIFICATE,SHOELENGTH,BODILYFORM_CODE,FACEFORM_CODE,ISEYEGLASS,SHOESIZE,BLOODTYPE_CODE,COLLECTIPADDRESS,COLLECT_TYPE_ID,STATUS from ['t_person_info'] where ((((q=query('湿的',default_field=allField)) OR allField=matchphrase('湿的')))) order by _score desc limit 0,1").explain();
//            System.out.println("result = " + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println( "Hello World!" );
        SpringApplication.run(App.class, args);
    }
}
