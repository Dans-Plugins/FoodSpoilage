package spoilagesystem.config.migration;

public interface ConfigMigration extends Runnable {

    String getPreviousVersion();
    String getNewVersion();

}
