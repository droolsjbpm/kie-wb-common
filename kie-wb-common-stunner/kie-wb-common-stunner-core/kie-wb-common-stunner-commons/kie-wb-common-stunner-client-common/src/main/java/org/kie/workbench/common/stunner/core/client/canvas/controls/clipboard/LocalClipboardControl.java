/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

@ApplicationScoped
@Default
public class LocalClipboardControl
        extends AbstractCanvasControl<AbstractCanvas>
        implements ClipboardControl<Element, AbstractCanvas, ClientSession> {

    private final Set<Element> elements;
    private final Map<String, String> elementsParent;
    private final List<Command> commands;

    public LocalClipboardControl() {
        this.elements = new HashSet<>();
        this.elementsParent = new HashMap<>();
        this.commands = new ArrayList<>();
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> set(Element... element) {
        clear();
        elements.addAll(Arrays.stream(element).collect(Collectors.toSet()));
        elementsParent.putAll(elements.stream().filter(e -> e instanceof Node).collect(Collectors.toMap(Element::getUUID, e -> GraphUtils.getParent(e.asNode()).getUUID())));
        return this;
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> remove(Element... element) {
        elements.removeAll(Arrays.stream(element).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public Collection<Element> getElements() {
        return elements;
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> clear() {
        commands.clear();
        elements.clear();
        elementsParent.clear();
        return this;
    }

    @Override
    public boolean hasElements() {
        return !elements.isEmpty();
    }

    @Override
    public String getParent(String uuid) {
        return elementsParent.get(uuid);
    }

    @Override
    public List<Command> getRollbackCommands() {
        return commands;
    }

    @Override
    public ClipboardControl<Element, AbstractCanvas, ClientSession> setRollbackCommand(Command... command) {
        commands.clear();
        commands.addAll(Stream.of(command).collect(Collectors.toList()));
        return this;
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        clear();
    }
}