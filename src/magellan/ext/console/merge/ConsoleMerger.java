package magellan.ext.console.merge;

import java.io.File;
import java.util.zip.ZipEntry;

import magellan.library.GameData;
import magellan.library.io.GameDataReader;
import magellan.library.io.cr.CRWriter;
import magellan.library.io.file.FileType;
import magellan.library.io.file.FileTypeFactory;
import magellan.library.io.file.FileTypeFactory.FileTypeChooser;
import magellan.library.utils.NullUserInterface;
import magellan.library.utils.PropertiesHelper;
import magellan.library.utils.ReportMerger;
import magellan.library.utils.Resources;

/**
 * This little program loads a CR report, merges another CR report
 * and saves the merges report to a third file.
 */
public class ConsoleMerger implements ReportMerger.Loader, ReportMerger.AssignData {
  private static File settingsDir = null;

  private File baseReport = null;
  private File mergeReport = null;
  private File resultReport = null;
  
  private GameData base = null;
  private GameData merged = null;
  
  /**
   *
   */
  public ConsoleMerger(File baseReport, File mergeReport, File resultReport) {
    this.baseReport = baseReport;
    this.mergeReport = mergeReport;
    this.resultReport = resultReport;
    
    PropertiesHelper.setSettingsDirectory(settingsDir);
    Resources.getInstance().initialize(settingsDir, "");
  }
  
  /**
   *
   */
  protected void check() {
    if (!baseReport.exists() || !baseReport.canRead() || !baseReport.isFile()) {
      System.out.println("base_report "+baseReport+" does not exist or isn't readable");
      System.exit(1);
    }
    
    if (!mergeReport.exists() || !mergeReport.canRead() || !mergeReport.isFile()) {
      System.out.println("merge_report "+mergeReport+" does not exist or isn't readable");
      System.exit(1);
    }
  }
  
  /**
   *
   */
  public void run() {
    System.out.println("Load Base Report: "+baseReport);
    base = load(baseReport);
    if (base == null) {
      System.exit(2);
      return;
    }
    
    System.out.println("Merge Report: "+mergeReport);
    ReportMerger merger = new ReportMerger(base, mergeReport, this, this);
    merged = merger.merge();
    
    if (merged == null) {
      System.exit(2);
      return;
    }
    
    try {
      System.out.println("Save Report to "+resultReport);
      FileType type = FileTypeFactory.singleton().createFileType(resultReport, false);
      NullUserInterface ui = new NullUserInterface();
      CRWriter crw = new CRWriter(ui,type,merged.getEncoding());
      crw.write(merged);
      crw.close();
    } catch (Exception exception) {
      exception.printStackTrace(System.out);
      System.exit(1);
    }
  }


  /**
   * Syntax: java magellan.ext.console.merge.ConsoleMerger <magellan_dir> <original_report> <merge_report> <result_report>
   */
  public static void main(String[] args) {
    System.setErr(System.out);
    
    System.out.println("ConsoleMerger V.1.0.1");
    System.out.println("Author: Thoralf Rickert <thoralf@m84.de>");
    System.out.println("");
    
//    args = new String[4];
//    args[0] = "D:/Eclipse Workspace/Magellan2";
//    args[1] = "D:/TEMP/519-x6j3.zip";
//    args[2] = "D:/TEMP/520-x6j3.zip";
//    args[3] = "D:/TEMP/report.cr.bz2";
    
    if (args.length != 4) {
      System.out.println("Syntax:");
      System.out.println("java magellan.ext.console.merge.ConsoleMerger <magellan_dir> <base_report> <merge_report> <result_report>");
      System.out.println("  magellan_dir  - the directory that contains the magellan settings (rules and resources).");
      System.out.println("  base_report   - the original report.");
      System.out.println("  merge_report  - the report that should be merged to <base_report>");
      System.out.println("  result_report - the destination file. If the file exists, it'll be overwritten.");
      System.out.println("This program is aware of the file name extension and loads and saves");
      System.out.println("the file in the given format.");
      System.exit(1);
    }
    
    settingsDir = new File(args[0]);
    if (!settingsDir.isDirectory()) {
      System.out.println("<magellan_dir> must be a directory.");
      System.exit(1);
    }
    
    ConsoleMerger merger = new ConsoleMerger(new File(args[1]),new File(args[2]),new File(args[3]));
    merger.check();
    merger.run();
    
    System.out.println("");
    System.out.println("OK");
  }

  /**
   * 
   */
  public GameData load(File file) {
    GameData data = null;
    NullUserInterface ui = new NullUserInterface();

    try {
      data = new GameDataReader(ui).readGameData(FileTypeFactory.singleton().createFileType(file, true, new NullFileTypeChooser()));
    } catch (FileTypeFactory.NoValidEntryException e) {
      System.out.println("Base Report contains no or multiple cr reports and Merger cannot choose the correct file.");
      System.exit(1);
    } catch (Exception exception) {
      exception.printStackTrace(System.out);
      System.exit(1);
    }

    if (data!=null && data.outOfMemory) {
      System.out.println("Running out of memory...take care!");
    }
    
    return data;
  }

  /**
   * 
   */
  public void assign(GameData data) {
    this.base = data;
  }

}

class NullFileTypeChooser extends FileTypeChooser {
  /**
   * @see magellan.library.io.file.FileTypeFactory.FileTypeChooser#chooseZipEntry(java.util.zip.ZipEntry[])
   */
  @Override
  public ZipEntry chooseZipEntry(ZipEntry[] entries) {
    for (ZipEntry entry : entries) {
      String name = entry.getName();
      if (name.toLowerCase().endsWith(".cr")) return entry;
    }
    return null;
  }
}