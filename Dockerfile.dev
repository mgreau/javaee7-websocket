FROM jboss/wildfly:10.0.0.Final

RUN touch /opt/jboss/wildfly/standalone/deployments/ROOT.war.dodeploy

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
