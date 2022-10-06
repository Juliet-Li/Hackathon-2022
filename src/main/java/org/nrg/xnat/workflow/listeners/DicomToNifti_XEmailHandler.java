package org.nrg.xnat.workflow.listeners;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.preferences.NotificationsPreferences;
import org.nrg.xft.event.entities.WorkflowStatusEvent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xnat.event.listeners.PipelineEmailHandlerAbst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import java.util.Map;

import static reactor.bus.selector.Selectors.R;

/**
 * Created by flavin on 3/2/15.
 */
@Service
public class DicomToNifti_XEmailHandler extends PipelineEmailHandlerAbst implements Consumer<Event<WorkflowStatusEvent>> {
    final static Logger logger = Logger.getLogger(DicomToNifti_XEmailHandler.class);

    private final String PIPELINE_NAME = "dcm2niix/DicomToNifti_X.xml";
    private final String PIPELINE_NAME_PRETTY = "DicomToNifti_X";

    @Autowired
    public DicomToNifti_XEmailHandler(EventBus eventBus){
        eventBus.on(R(WorkflowStatusEvent.class.getName() + "[.]?(" + PersistentWorkflowUtils.COMPLETE + "|" + PersistentWorkflowUtils.FAILED + ")"), this);
    }

    /* (non-Javadoc)
     * @see reactor.fn.Consumer#accept(java.lang.Object)
     */
    @Override
    public void accept(Event<WorkflowStatusEvent> event) {
        final WorkflowStatusEvent wfsEvent = event.getData();
        if (wfsEvent.getWorkflow() instanceof WrkWorkflowdata) {
            handleEvent(wfsEvent);
        }
    }

    public void handleEvent(WorkflowStatusEvent e) {
        WrkWorkflowdata wrk = (WrkWorkflowdata)e.getWorkflow();
		Map<String,Object> params = Maps.newHashMap();
        params.put("pipelineName",PIPELINE_NAME_PRETTY);
        if (completed(e)) {
            standardPipelineEmailImpl(e, wrk, PIPELINE_NAME, DEFAULT_TEMPLATE_SUCCESS, DEFAULT_SUBJECT_SUCCESS, "processed.lst", params);
        } else if (failed(e)) {
            standardPipelineEmailImpl(e, wrk, PIPELINE_NAME, DEFAULT_TEMPLATE_FAILURE, DEFAULT_SUBJECT_FAILURE, "processed.lst", params);
        }
    }
}
