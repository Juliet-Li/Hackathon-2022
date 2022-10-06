/*
 *  Copyright Washington University in St Louis 2006
 *  All rights reserved
 *
 *  @author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.pipeline.launchers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.pipeline.XnatPipelineLauncher;
import org.nrg.pipeline.utils.PipelineFileUtils;
import org.nrg.pipeline.utils.PipelineUtils;
import org.nrg.pipeline.xmlbeans.ParameterData;
import org.nrg.pipeline.xmlbeans.ParameterData.Values;
import org.nrg.pipeline.xmlbeans.ParametersDocument.Parameters;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.model.XnatMrsessiondataI;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;

public class HCPDicomToNiftiLauncher extends PipelineLauncher{

    public static final String NAME = "HCPDicomToNifti.xml";
    public static final String LOCATION = "HCP";
    public static final String TEMPLATE = "PipelineScreen_HCPDicomToNifti.vm";
    public static final String PIPELINENAME = LOCATION+File.separator+NAME;

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HCPDicomToNiftiLauncher.class);

    public boolean launch(RunData data, Context context) {
        try {
            ItemI data_item = TurbineUtils.GetItemBySearch(data);
            XnatMrsessiondata mr = new XnatMrsessiondata(data_item);
            XnatPipelineLauncher xnatPipelineLauncher = XnatPipelineLauncher.GetLauncher(data, context, mr);
            String pipelineName = PIPELINENAME;
            String cmdPrefix = data.getParameters().get("cmdprefix");
            xnatPipelineLauncher.setPipelineName(pipelineName);
            String buildDir = PipelineFileUtils.getBuildDir(mr.getProject(), true);
            buildDir += "DicomToNifti";
            xnatPipelineLauncher.setBuildDir(buildDir);
            xnatPipelineLauncher.setNeedsBuildDir(false);

            Parameters parameters = Parameters.Factory.newInstance();

            /* PARAMETERS */
            /*  scanids
                    Checkboxes -> csv
                create_nii
                    checkbox -> <csv>Y,N</csv>
                keep_qc
                    checkbox -> <csv>Y,N</csv>
                overwrite_existing
                    checkbox -> <csv>Y,N</csv>
                notify
                    checkbox -> <csv>Y,N</csv>
                structural_scan_types
                    textbox -> csv */

            ParameterData param = parameters.addNewParameter();

            param.setName("sessionId");
            param.addNewValues().setUnique(mr.getLabel());

            param = parameters.addNewParameter();
            param.setName("subject");
            param.addNewValues().setUnique(mr.getSubjectData().getLabel());

            param = parameters.addNewParameter();
            param.setName("xnat_id");
            param.addNewValues().setUnique(mr.getId());

            param = parameters.addNewParameter();
            param.setName("create_nii");
            param.addNewValues().setUnique(data.getParameters().getString("create_nii"));

            param = parameters.addNewParameter();
            param.setName("keep_qc");
            if (TurbineUtils.HasPassedParameter("keep_qc", data)) {
                param.addNewValues().setUnique(data.getParameters().getBoolean("keep_qc")?"Y":"N");
            }else {
                param.addNewValues().setUnique("N");
            }

            param = parameters.addNewParameter();
            param.setName("overwrite_existing");
            if (TurbineUtils.HasPassedParameter("overwrite_existing", data)) {
                param.addNewValues().setUnique(data.getParameters().getBoolean("overwrite_existing")?"Y":"N");
            }else {
                param.addNewValues().setUnique("N");
            }

            param = parameters.addNewParameter();
            param.setName("notify");
            if (TurbineUtils.HasPassedParameter("notify", data)) {
                param.addNewValues().setUnique(data.getParameters().getBoolean("notify")?"Y":"N");
            }else {
                param.addNewValues().setUnique("N");
            }

            // scan list
            ArrayList<String> scans = getCheckBoxSelections(data,mr,"scan");
            param = parameters.addNewParameter();
            param.setName("scanids");
            Values values = param.addNewValues();
            if (scans.size() == 1) {
                values.setUnique(scans.get(0));
            }else {
                for (int i = 0; i < scans.size(); i++) {
                    values.addList(scans.get(i));
                }
            }

            // Structural scan type list
            param = parameters.addNewParameter();
            param.setName("structural_scan_types");
            String structuralScanTypesStr = ((String)org.nrg.xdat.turbine.utils.TurbineUtils.GetPassedParameter("structural_scan_types",data));
            param.addNewValues().setUnique(structuralScanTypesStr);

            String emailsStr = TurbineUtils.getUser(data).getEmail() + "," + data.getParameters().get("emailField");
            String[] emails = emailsStr.trim().split(",");
            for (int i = 0 ; i < emails.length; i++) {
                if (emails[i]!=null && !emails[i].equals("")) xnatPipelineLauncher.notify(emails[i]);
            }

            String paramFileName = getName(pipelineName);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String s = formatter.format(date);

            paramFileName += "_params_" + s + ".xml";

            String paramFilePath = saveParameters(buildDir + File.separator + mr.getLabel(),paramFileName,parameters);

            xnatPipelineLauncher.setParameterFile(paramFilePath);

            return xnatPipelineLauncher.launch(cmdPrefix);
        }catch(Exception e) {
            logger.error(e.getCause() + " " + e.getLocalizedMessage());
            return false;
        }
    }

}
