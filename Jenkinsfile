pipeline {
    agent any
    parameters {
        booleanParam(name: 'DEPLOY_REMOTE_DB', defaultValue: false,
                     description: 'Reinicia dbgestor en Railway e importa el dump (requiere TCP Proxy abierto)')
    }
    stages {
        stage('Checkout SQL Dump') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/gus-p3/U2_GPDS_Instrumento.git'
            }
        }
        stage('Ver Directorio y Estado MariaDB') {
            steps {
                sh 'ls -lh dump-dbgestor-202406261453.sql'
                withCredentials([string(credentialsId: 'mariadb-root-password', variable: 'DB_PASS')]) {
                    sh '''
                        if command -v docker >/dev/null 2>&1; then
                            docker exec -e MYSQL_PWD="$DB_PASS" mariadb-container mariadb -uroot -e "SELECT VERSION() AS MariaDB_Version, CURRENT_USER() AS Usuario_Conectado;"
                        else
                            MYSQL_PWD="$DB_PASS" mariadb -h "${DB_HOST:-mariadb.railway.internal}" -P "${DB_PORT:-3306}" -uroot -e "SELECT VERSION() AS MariaDB_Version, CURRENT_USER() AS Usuario_Conectado;"
                        fi
                    '''
                }
            }
        }
        stage('Provision & Import Schema (dbgestor)') {
            steps {
                withCredentials([string(credentialsId: 'mariadb-root-password', variable: 'DB_PASS')]) {
                    sh '''
                        if command -v docker >/dev/null 2>&1; then
                            docker exec -e MYSQL_PWD="$DB_PASS" mariadb-container mariadb -uroot -e "CREATE DATABASE IF NOT EXISTS dbgestor;"
                            docker exec -i -e MYSQL_PWD="$DB_PASS" mariadb-container mariadb -uroot dbgestor < dump-dbgestor-202406261453.sql
                        else
                            echo "Entorno Railway sin Docker local: se omite la importacion local destructiva (usa el parametro DEPLOY_REMOTE_DB para reimportar en Railway)."
                        fi
                    '''
                }
            }
        }
        stage('DB Smoke Tests & Integrity Validation') {
            steps {
                withCredentials([string(credentialsId: 'mariadb-root-password', variable: 'DB_PASS')]) {
                    sh '''
                        if command -v docker >/dev/null 2>&1; then
                            docker exec -e MYSQL_PWD="$DB_PASS" mariadb-container mariadb -uroot dbgestor -e "SHOW TABLES;"
                            docker exec -e MYSQL_PWD="$DB_PASS" mariadb-container mariadb -uroot dbgestor -e "
                                SELECT 'usuario' AS Tabla, COUNT(*) AS Total_Registros FROM usuario
                                UNION ALL
                                SELECT 'proyectos', COUNT(*) FROM proyectos
                                UNION ALL
                                SELECT 'tareas', COUNT(*) FROM tareas
                                UNION ALL
                                SELECT 'comentarios', COUNT(*) FROM comentarios
                                UNION ALL
                                SELECT 'asignaciones', COUNT(*) FROM asignaciones;
                            "
                        else
                            MYSQL_PWD="$DB_PASS" mariadb -h "${DB_HOST:-mariadb.railway.internal}" -P "${DB_PORT:-3306}" -uroot dbgestor -e "SHOW TABLES;"
                            MYSQL_PWD="$DB_PASS" mariadb -h "${DB_HOST:-mariadb.railway.internal}" -P "${DB_PORT:-3306}" -uroot dbgestor -e "
                                SELECT 'usuario' AS Tabla, COUNT(*) AS Total_Registros FROM usuario
                                UNION ALL
                                SELECT 'proyectos', COUNT(*) FROM proyectos
                                UNION ALL
                                SELECT 'tareas', COUNT(*) FROM tareas
                                UNION ALL
                                SELECT 'comentarios', COUNT(*) FROM comentarios
                                UNION ALL
                                SELECT 'asignaciones', COUNT(*) FROM asignaciones;
                            "
                        fi
                    '''
                }
            }
        }
        stage('Security Scan (SAST) - Semgrep') {
            steps {
                sh '''
                    mkdir -p reportes
                    if command -v semgrep >/dev/null 2>&1; then
                        semgrep scan \
                            --config=auto \
                            --json \
                            --output=reportes/semgrep.json \
                            dump-dbgestor-202406261453.sql
                    else
                        docker run --rm \
                        --network bridge \
                        -v "${WORKSPACE}":/src \
                        -w /src \
                        semgrep/semgrep \
                        semgrep scan \
                        --config=auto \
                        --json \
                        --output=reportes/semgrep.json \
                        dump-dbgestor-202406261453.sql
                    fi
                '''
            }
        }
        stage('Deploy to Railway') {
            when { expression { return params.DEPLOY_REMOTE_DB } }
            steps {
                withCredentials([
                    string(credentialsId: 'railway-db-host', variable: 'RW_HOST'),
                    string(credentialsId: 'railway-db-port', variable: 'RW_PORT'),
                    string(credentialsId: 'railway-db-root-password', variable: 'RW_PASS')
                ]) {
                    sh '''
                        export MYSQL_PWD="$RW_PASS"
                        if command -v docker >/dev/null 2>&1; then
                            MARIA="docker exec -i -e MYSQL_PWD mariadb-container mariadb -h $RW_HOST -P $RW_PORT -uroot"
                        else
                            MARIA="mariadb -h $RW_HOST -P $RW_PORT -uroot"
                        fi
                        SQL="DROP DATABASE IF EXISTS dbgestor; CREATE DATABASE dbgestor CHARACTER SET utf8mb4;"
                        SQL="$SQL GRANT ALL PRIVILEGES ON dbgestor.* TO 'gestor'@'%'; FLUSH PRIVILEGES;"
                        $MARIA -e "$SQL"
                        $MARIA --default-character-set=utf8mb4 dbgestor < dump-dbgestor-202406261453.sql
                        $MARIA dbgestor -e "SELECT COUNT(*) AS usuarios FROM usuario;"
                    '''
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'reportes/semgrep.json',
            allowEmptyArchive: true,
            fingerprint: true
        }
    }
}
