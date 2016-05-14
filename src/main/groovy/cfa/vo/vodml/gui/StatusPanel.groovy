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
package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Specialization of {@link JPanel} for a Status Panel that is updated through a special logger.
 *
 * Simply grab the logger from {@link #STATUS_LOGGER} and log to it as usual. Messages will be used
 * to set the text of this panel's status label.
 *
 * For convenience, a {@link #setStatus(String)} setter is provided for setting the status text directly.
 * However, this should be used only to
 */
class StatusPanel extends JPanel {
    private JLabel statusLabel
    private SwingBuilder swing = new SwingBuilder()
    public static final Logger STATUS_LOGGER = Logger.getLogger("status")

    public StatusPanel() {
        swing.build {
            statusLabel = label(name: "status", border: BorderFactory.createEmptyBorder(5,5,5,5))
        }
        layout = new BorderLayout()
        border = BorderFactory.createBevelBorder(1)
        add(statusLabel)

        STATUS_LOGGER.addHandler(new StatusHandler())
    }

    public void setStatus(String status) {
        statusLabel.text = status
    }

    private class StatusHandler extends Handler {

        @Override
        void publish(LogRecord record) {
            swing.edt {
                statusLabel.text = "${record.level}: ${record.message}"
            }
        }

        @Override
        void flush() {
        }

        @Override
        void close() throws SecurityException {
        }
    }
}
