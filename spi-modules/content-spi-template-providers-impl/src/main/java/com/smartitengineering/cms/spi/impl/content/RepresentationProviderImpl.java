/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2010  Imran M Yousuf (imyousuf@smartitengineering.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.cms.spi.impl.content;

import com.smartitengineering.cms.spi.impl.AbstractRepresentationProvider;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.MutableRepresentation;
import com.smartitengineering.cms.api.content.Representation;
import com.smartitengineering.cms.api.content.template.RepresentationGenerator;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.RepresentationDef;
import com.smartitengineering.cms.api.workspace.RepresentationTemplate;
import com.smartitengineering.cms.spi.content.RepresentationProvider;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class RepresentationProviderImpl extends AbstractRepresentationProvider implements RepresentationProvider {

  @Override
  public Representation getRepresentation(String repName, ContentTypeId contentTypeId, ContentId contentId) {
    return getRepresentation(repName, contentTypeId, SmartContentAPI.getInstance().getContentLoader().loadContent(
        contentId));
  }

  @Override
  public Representation getRepresentation(String repName, ContentTypeId contentTypeId, Content content) {
    return getRepresentation(repName,
                             SmartContentAPI.getInstance().getContentTypeLoader().loadContentType(contentTypeId),
                             content);
  }

  @Override
  public Representation getRepresentation(String repName, ContentType contentType, Content content) {
    if (StringUtils.isBlank(repName) || contentType == null || content == null) {
      logger.info("Representation name or content type or content is null or blank!");
      return null;
    }
    RepresentationDef def = contentType.getRepresentationDefs().get(repName);
    RepresentationGenerator generator = SmartContentAPI.getInstance().getWorkspaceApi().getRepresentationGenerator(content.
        getContentId().getWorkspaceId(), def.getResourceUri().getValue());
    if (generator == null) {
      logger.info("Representation generator is null!");
      return null;
    }
    final MutableRepresentation representation = getMutableRepresentation(generator, content, def.getParameters(),
                                                                          repName);
    representation.setLastModifiedDate(new Date());
    return representation;
  }

  @Override
  public boolean isValidTemplate(RepresentationTemplate representationTemplate) {
    if (representationTemplate == null) {
      logger.info("Representation template is null!");
      return false;
    }
    RepresentationGenerator generator = SmartContentAPI.getInstance().getWorkspaceApi().getRepresentationGenerator(
        representationTemplate);
    if (generator == null) {
      logger.info("Representation generator is null!");
      return false;
    }
    return true;
  }
}
