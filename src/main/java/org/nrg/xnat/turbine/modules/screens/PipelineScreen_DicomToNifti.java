package org.nrg.xnat.turbine.modules.screens;

import org.apache.log4j.Logger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.turbine.utils.TurbineUtils;

public class PipelineScreen_DicomToNifti extends DefaultPipelineScreen  {

    static Logger logger = Logger.getLogger(PipelineScreen_DicomToNifti.class);

    public void finalProcessing(RunData data, Context context){
        try {
            String pipelinePath = (String)TurbineUtils.GetPassedParameter("pipeline",data);
            context.put("pipelinePath",pipelinePath);

            context.put("projectSettings", projectParameters);
        }catch(Exception e) {
            logger.error("Possibly the project wide pipeline has not been set", e);
            e.printStackTrace();
        }
    }
}
