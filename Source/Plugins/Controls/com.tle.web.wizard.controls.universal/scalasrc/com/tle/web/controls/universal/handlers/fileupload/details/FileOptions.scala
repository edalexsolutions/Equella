package com.tle.web.controls.universal.handlers.fileupload.details

import com.tle.common.wizard.controls.universal.handlers.FileUploadSettings
import com.tle.web.controls.universal.handlers.fileupload.packages.PackageFileCreate
import com.tle.web.controls.universal.handlers.fileupload.{PackageType, ValidatedUpload, WebFileUploads}
import com.tle.web.controls.universal.{AbstractScalaSection, ControlContext, DialogRenderOptions, RenderHelper}
import com.tle.web.freemarker.FreemarkerFactory
import com.tle.web.freemarker.annotations.ViewFactory
import com.tle.web.sections.annotations.{EventFactory, EventHandlerMethod}
import com.tle.web.sections.equella.annotation.PlugKey
import com.tle.web.sections.events.RenderContext
import com.tle.web.sections.events.js.EventGenerator
import com.tle.web.sections.render.{Label, SectionRenderable}
import com.tle.web.sections.standard.annotations.Component
import com.tle.web.sections.standard.model.{DynamicHtmlListModel, LabelOption}
import com.tle.web.sections.standard.{Button, SingleSelectionList}
import com.tle.web.sections.{SectionInfo, SectionTree}

import scala.collection.JavaConverters._

object FileOptions {
  val TITLE = WebFileUploads.label("handlers.file.packageoption.title")
  val LABEL_TREAT_AS_FILE = WebFileUploads.label("handlers.file.packageoptions.asfile")
}

import com.tle.web.controls.universal.handlers.fileupload.details.FileOptions._

class FileOptions(parentId: String, tree: SectionTree, settings: FileUploadSettings,
                  ctx: ControlContext,
                  currentUpload: SectionInfo => ValidatedUpload,
                  resolved: (SectionInfo, ValidatedUpload) => Unit)
  extends AbstractScalaSection with RenderHelper {
  type M = Model

  class Model(info: SectionInfo) {
    lazy val vu = currentUpload(info)
  }

  override def newModel: (SectionInfo) => Model = new Model(_)

  @ViewFactory var viewFactory: FreemarkerFactory = _
  @EventFactory var events: EventGenerator = _
  @Component var packageOptions: SingleSelectionList[TreatAsOption] = _
  @Component
  @PlugKey("handlers.file.action.next") var optionsButton: Button = _

  @EventHandlerMethod def nextPage(info: SectionInfo) = {
    val vu = getModel(info).vu
    val selType = packageOptions.getSelectedValue(info)
    resolved(info, selType.value match {
      case "file" => vu.copy(detected = Seq.empty)
      case o => vu.copy(detected = Seq(PackageType.fromString(o)))
    })
  }

  tree.registerSubInnerSection(this, parentId)

  override def getDefaultPropertyName = "po"

  val defaultOptions = if (!settings.isPackagesOnly) Seq(TreatAsOption(LABEL_TREAT_AS_FILE, "file")) else Seq.empty

  def defaultResolve(vu: ValidatedUpload): Option[ValidatedUpload] = {
    if ((settings.isPackagesOnly && (settings.isQtiPackagesOnly || settings.isScormPackagesOnly || vu.detected.size == 1)) ||
      (!settings.isPackagesOnly && vu.detected.isEmpty)) Some(vu)
    else None
  }

  case class TreatAsOption(label: Label, value: String)

  class PackageEditOptionsModel {
    def getEditTitle = TITLE

    def getPackageOptions = packageOptions
  }

  def prepareUI(info: SectionInfo, vu: ValidatedUpload): Unit = {

  }

  def render(context: RenderContext, vu: ValidatedUpload): (SectionRenderable, DialogRenderOptions => Unit) = {
    (renderModel("file/file-packageoptions.ftl", new PackageEditOptionsModel), _.addAction(optionsButton))
  }

  override def registered(id: String, tree: SectionTree): Unit = {
    super.registered(id, tree)
    packageOptions.setListModel(new PackageOptionsListModel)
    packageOptions.setAlwaysSelect(true)
    optionsButton.setClickHandler(events.getNamedHandler("nextPage"))
  }

  class PackageOptionsListModel extends DynamicHtmlListModel[TreatAsOption] {
    override protected def populateModel(info: SectionInfo): java.lang.Iterable[TreatAsOption] = {
      val dynamicOptions = getModel(info).vu.detected.map { pt =>
        val t = PackageType.packageTypeString(pt)
        TreatAsOption(PackageFileCreate.packageCreateById(t).treatAsLabel, t)
      }
      (dynamicOptions ++ defaultOptions).asJava
    }

    override protected def convertToOption(info: SectionInfo, obj: TreatAsOption) =
      new LabelOption[TreatAsOption](obj.label, obj.value, obj)
  }


}