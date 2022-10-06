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
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;

public class DicomToNiftiLauncher extends PipelineLauncher {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DicomToNiftiLauncher.class);

    public boolean launch(RunData data, Context context) {
        try {
            ItemI data_item = TurbineUtils.GetItemBySearch(data);
            XnatImagesessiondata imageSession = new XnatImagesessiondata(data_item);
            XnatPipelineLauncher xnatPipelineLauncher = XnatPipelineLauncher.GetLauncher(data, context, imageSession);

            String pipelineLocation = ((String)TurbineUtils.GetPassedParameter("pipeline_location",data));
            xnatPipelineLauncher.setPipelineName(pipelineLocation);

            String buildDir = PipelineFileUtils.getBuildDir(imageSession.getProject(), true);
            xnatPipelineLauncher.setBuildDir(buildDir);
            xnatPipelineLauncher.setNeedsBuildDir(false);

            Parameters parameters = Parameters.Factory.newInstance();
            ParameterData param = parameters.addNewParameter();
            param.setName("overwrite");
            param.addNewValues().setUnique(data.getParameters().getString("overwrite"));

            String paramFileName = getName(pipelineLocation);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String s = formatter.format(date);

            paramFileName += "_params_" + s + ".xml";

            String paramFilePath = saveParameters(buildDir + File.separator + imageSession.getLabel(),paramFileName,parameters);

            xnatPipelineLauncher.setParameterFile(paramFilePath);

            return xnatPipelineLauncher.launch();
        }catch(Exception e) {
            logger.error(e.getCause() + " " + e.getLocalizedMessage());
            return false;
        }
    }
}
