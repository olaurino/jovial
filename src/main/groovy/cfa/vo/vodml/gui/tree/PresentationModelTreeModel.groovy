/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.gui.tree

import cfa.vo.vodml.gui.PresentationModel

import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class PresentationModelTreeModel implements TreeModel {
    private List<TreeModelListener> listeners = new ArrayList<>()
    ModelTreeNode model

    public PresentationModelTreeModel(PresentationModel model) {
        this.model = new ModelTreeNode(model)
        model.propertyChange = {
            listeners.each {
                it.treeStructureChanged(new TreeModelEvent(model, new TreePath(model)))
            }
        }
    }

    @Override
    Object getRoot() {
        return model
    }

    @Override
    Object getChild(Object parent, int index) {
        parent.getChildAt(index)
    }

    @Override
    int getChildCount(Object parent) {
        return parent.getChildCount()
    }

    @Override
    boolean isLeaf(Object node) {
        node.isLeaf()
    }

    @Override
    void valueForPathChanged(TreePath path, Object newValue) {
        listeners.each {
            it.treeNodesChanged(new TreeModelEvent(newValue, path))
        }
    }

    @Override
    int getIndexOfChild(Object parent, Object child) {
        return parent.getIndex(child)
    }

    @Override
    void addTreeModelListener(TreeModelListener l) {
        listeners.add(l)
    }

    @Override
    void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l)
    }
}
