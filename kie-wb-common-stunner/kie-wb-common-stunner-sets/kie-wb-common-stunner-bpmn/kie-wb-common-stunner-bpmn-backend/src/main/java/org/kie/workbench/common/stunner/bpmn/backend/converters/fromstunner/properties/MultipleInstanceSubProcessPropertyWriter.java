/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class MultipleInstanceSubProcessPropertyWriter extends SubProcessPropertyWriter {

    private final MultiInstanceLoopCharacteristics miloop;
    private final InputOutputSpecification ioSpec;
    private final InputSet inputSet;
    private final OutputSet outputSet;

    public MultipleInstanceSubProcessPropertyWriter(SubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.miloop = bpmn2.createMultiInstanceLoopCharacteristics();
        process.setLoopCharacteristics(miloop);
        this.ioSpec = bpmn2.createInputOutputSpecification();
        this.inputSet = bpmn2.createInputSet();
        this.ioSpec.getInputSets().add(inputSet);
        this.outputSet = bpmn2.createOutputSet();
        this.ioSpec.getOutputSets().add(outputSet);

        process.setIoSpecification(ioSpec);
    }

    public void setInput(String collectionInput, String dataInput) {
        // ignore empty input
        if (collectionInput == null) {
            return;
        }
        DataInput dataInputElement = setDataInput(dataInput);
        Property prop = findPropertyById(collectionInput);
        miloop.setLoopDataInputRef(prop);

        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
        ie.setId(collectionInput);
        dia.getSourceRef().add(ie);
        dia.setTargetRef(dataInputElement);
        process.getDataInputAssociations().add(dia);

    }

    public void setOutput(String collectionOutput, String dataOutput) {
        // ignore empty input
        if (collectionOutput == null) {
            return;
        }
        DataOutput dataOutputElement = setDataOutput(dataOutput);
        Property prop = findPropertyById(collectionOutput);
        miloop.setLoopDataOutputRef(prop);

        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
        ie.setId(collectionOutput);
        doa.getSourceRef().add(dataOutputElement);
        doa.setTargetRef(ie);
        process.getDataOutputAssociations().add(doa);
    }

    private Property findPropertyById(String id) {
        return variableScope.lookup(id).getTypedIdentifier();
    }

    public DataInput setDataInput(String value) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(value);
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(process.getId(), "IN"));
        this.addItemDefinition(item);
        dataInput.setItemSubjectRef(item);
        this.ioSpec.getDataInputs().add(dataInput);
        this.inputSet.getDataInputRefs().add(dataInput);
        this.miloop.setLoopDataInputRef(dataInput);
        this.miloop.setInputDataItem(dataInput);
        return dataInput;
    }

    public DataOutput setDataOutput(String value) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(value);
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(process.getId(), "OUT"));
        this.addItemDefinition(item);
        dataOutput.setItemSubjectRef(item);
        this.ioSpec.getDataOutputs().add(dataOutput);
        this.outputSet.getDataOutputRefs().add(dataOutput);
        this.miloop.setLoopDataOutputRef(dataOutput);
        this.miloop.setOutputDataItem(dataOutput);
        return dataOutput;
    }

    public void setCompletionCondition(String expression) {
        FormalExpression formalExpression = bpmn2.createFormalExpression();
        formalExpression.setBody(expression);
        this.miloop.setCompletionCondition(formalExpression);
    }
}
