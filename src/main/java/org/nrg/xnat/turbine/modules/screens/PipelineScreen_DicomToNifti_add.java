package org.nrg.xnat.turbine.modules.screens;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

public class PipelineScreen_DicomToNifti_add extends PipelineScreen_add_project_pipeline {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PipelineScreen_DicomToNifti_add.class);

    public void finalProcessing(RunData data, Context context) {
        logger.debug("Finished PipelineScreen_DicomToNifti_add");
    }
}
