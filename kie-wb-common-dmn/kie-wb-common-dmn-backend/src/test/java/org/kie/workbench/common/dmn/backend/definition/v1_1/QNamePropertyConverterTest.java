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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import javax.xml.XMLConstants;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;

public class QNamePropertyConverterTest {

    @Test
    public void testWBfromDMNnull() {
        final QName wb = QNamePropertyConverter.wbFromDMN(null);

        assertThat(wb).isNull();
    }

    @Test
    public void testWBfromDMNForBuiltInDataType11() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(XMLConstants.NULL_NS_URI,
                                                                            BuiltInType.STRING.getName(),
                                                                            Namespace.FEEL.getPrefix());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(QName.NULL_NS_URI);
        assertThat(wb.getLocalPart()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(wb.getPrefix()).isEqualTo(QName.DEFAULT_NS_PREFIX);
    }

    @Test
    public void testWBfromDMNForBuiltInDataType12() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(XMLConstants.NULL_NS_URI,
                                                                            BuiltInType.STRING.getName());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(QName.NULL_NS_URI);
        assertThat(wb.getLocalPart()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(wb.getPrefix()).isEqualTo(QName.DEFAULT_NS_PREFIX);
    }

    @Test
    public void testWBfromDMNCustomDataType() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(Namespace.KIE.getUri(),
                                                                            "tCustom",
                                                                            Namespace.KIE.getPrefix());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(Namespace.KIE.getUri());
        assertThat(wb.getLocalPart()).isEqualTo("tCustom");
        assertThat(wb.getPrefix()).isEqualTo(Namespace.KIE.getPrefix());
    }
}
