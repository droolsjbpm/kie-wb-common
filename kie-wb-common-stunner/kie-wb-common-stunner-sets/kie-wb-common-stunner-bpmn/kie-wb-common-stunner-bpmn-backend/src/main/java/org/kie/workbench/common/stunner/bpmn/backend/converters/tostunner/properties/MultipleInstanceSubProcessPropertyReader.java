package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

public class MultipleInstanceSubProcessPropertyReader extends SubProcessPropertyReader {

    public MultipleInstanceSubProcessPropertyReader(SubProcess element, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(element, plane, definitionResolver);
    }

    public String getCollectionInput() {
        MultiInstanceLoopCharacteristics loopCharacteristics = getMultiInstanceLoopCharacteristics();
        ItemAwareElement ieDataInput = loopCharacteristics.getLoopDataInputRef();
        return process.getDataInputAssociations().stream()
                .filter(dia -> dia.getTargetRef().equals(ieDataInput))
                .map(dia -> dia.getSourceRef().get(0).getId())
                .findFirst()
                .get();
    }

    public String getCollectionOutput() {
        MultiInstanceLoopCharacteristics loopCharacteristics = getMultiInstanceLoopCharacteristics();
        ItemAwareElement ieDataOutput = loopCharacteristics.getLoopDataOutputRef();
        return process.getDataOutputAssociations().stream()
                .filter(doa -> doa.getSourceRef().get(0).equals(ieDataOutput))
                .map(doa -> doa.getTargetRef().getId())
                .findFirst()
                .get();
    }

    public String getDataInput() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        return miloop.getInputDataItem().getId();
    }

    public String getDataOutput() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        return miloop.getOutputDataItem().getId();
    }

    public String getCompletionCondition() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        FormalExpression completionCondition = (FormalExpression) miloop.getCompletionCondition();
        return completionCondition.getBody();
    }

    public String getTrigger() {
        return "true";
    }

    private MultiInstanceLoopCharacteristics getMultiInstanceLoopCharacteristics() {
        return (MultiInstanceLoopCharacteristics) process.getLoopCharacteristics();
    }
}
