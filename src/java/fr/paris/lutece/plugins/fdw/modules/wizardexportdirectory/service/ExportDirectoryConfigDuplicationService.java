/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.fdw.modules.wizardexportdirectory.service;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.fdw.modules.wizard.business.DuplicationContext;
import fr.paris.lutece.plugins.fdw.modules.wizard.exception.DuplicationException;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.DuplicationService;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.IFormDirectoryAssociationService;
import fr.paris.lutece.plugins.fdw.modules.wizard.service.WizardService;
import fr.paris.lutece.plugins.fdw.modules.wizardexportdirectory.service.utils.DirectoryEntryMatcher;
import fr.paris.lutece.plugins.fdw.modules.wizardexportdirectory.service.utils.FormEntryMatcher;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.EntryConfiguration;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.EntryConfigurationHome;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfiguration;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfigurationHome;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.Collection;


/**
 * Duplication service
 * 
 */
public class ExportDirectoryConfigDuplicationService extends DuplicationService
    implements IFormDirectoryAssociationService
{
    private static final String PLUGIN_NAME = "fdw-wizardexportdirectory";
    private WizardService _wizardService;

    /**
     * @param wizardService the wizardService to set
     */
    public void setWizardService( WizardService wizardService )
    {
        this._wizardService = wizardService;
    }

    @Override
    public Directory getDirectoryAssociatedToForm( Form form )
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        Directory directory = null;
        FormConfiguration formConfiguration = FormConfigurationHome.findByPrimaryKey( form.getIdForm( ), plugin );

        if ( formConfiguration != null )
        {
            directory = _wizardService.getDirectory( formConfiguration.getIdDirectory( ), plugin );
        }

        return directory;
    }

    @Override
    public void doDuplicate( DuplicationContext context ) throws DuplicationException
    {
        // duplicates the directory associated to a form
        if ( context.isFormDuplication( ) && context.isDirectoryDuplication( ) )
        {
            Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

            Form formToCopy = context.getFormToCopy( );
            Form formCopy = context.getFormCopy( );

            int nIdDirectoryCopy = DirectoryUtils.CONSTANT_ID_NULL;
            int nIdFormCopy = GenericAttributesUtils.CONSTANT_ID_NULL;
            int nIdWorkflowCopy = WorkflowUtils.CONSTANT_ID_NULL;

            try
            {
                nIdFormCopy = formCopy.getIdForm( );

                Directory directoryToCopy = getDirectoryAssociatedToForm( formToCopy );

                if ( context.isWorkflowDuplication( ) )
                {
                    // directory + workflow
                    nIdDirectoryCopy = _wizardService.doCopyDirectoryWithWorkflow( directoryToCopy,
                            context.getDirectoryCopyName( ), context.getWorkflowCopyName( ), plugin,
                            context.getLocale( ) );
                }
                else
                {
                    // directory only
                    nIdDirectoryCopy = _wizardService.doCopyDirectory( directoryToCopy,
                            context.getDirectoryCopyName( ), plugin );
                }

                Directory directoryCopy = _wizardService.getDirectory( nIdDirectoryCopy, plugin );
                nIdWorkflowCopy = directoryCopy.getIdWorkflow( );

                directoryToCopy = getDirectoryAssociatedToForm( formToCopy );

                // duplicates export-directory configuration
                // form configuration
                FormConfiguration formConfigurationCopy = new FormConfiguration( );
                formConfigurationCopy.setIdForm( formCopy.getIdForm( ) );
                formConfigurationCopy.setIdDirectory( nIdDirectoryCopy );
                FormConfigurationHome.insert( formConfigurationCopy, plugin );

                // entry configuration
                Collection<EntryConfiguration> collectionEntryConfigurationToCopy = EntryConfigurationHome
                        .findEntryConfigurationListByIdForm( formToCopy.getIdForm( ), plugin );

                for ( EntryConfiguration entryConfiguration : collectionEntryConfigurationToCopy )
                {
                    int nIdFormEntryCopy = FormEntryMatcher.findMatchingIdEntry( entryConfiguration.getIdFormEntry( ),
                            formToCopy, formCopy, plugin );

                    int nIdDirectoryEntryCopy = DirectoryEntryMatcher.findMatchingIdEntry(
                            entryConfiguration.getIdDirectoryEntry( ), directoryToCopy, directoryCopy, plugin );

                    EntryConfiguration entryConfigurationCopy = new EntryConfiguration( );
                    entryConfigurationCopy.setIdForm( formCopy.getIdForm( ) );
                    entryConfigurationCopy.setIdFormEntry( nIdFormEntryCopy );
                    entryConfigurationCopy.setIdDirectoryEntry( nIdDirectoryEntryCopy );

                    EntryConfigurationHome.insert( entryConfigurationCopy, plugin );
                }

                // update context for other services
                context.setDirectoryToCopy( directoryToCopy );
                context.setDirectoryCopy( directoryCopy );
            }
            catch ( Exception e )
            {
                //rollback - delete copied directory and config
                if ( nIdFormCopy > 0 )
                {
                    FormConfigurationHome.delete( nIdFormCopy, plugin );
                    EntryConfigurationHome.deleteByForm( nIdFormCopy, plugin );
                }

                if ( nIdDirectoryCopy > 0 )
                {
                    DirectoryHome.remove( nIdDirectoryCopy, plugin );
                }

                if ( nIdWorkflowCopy > 0 )
                {
                    _wizardService.deleteWorkflow( nIdWorkflowCopy );
                }

                throw new DuplicationException( e );
            }
        }
    }
}
