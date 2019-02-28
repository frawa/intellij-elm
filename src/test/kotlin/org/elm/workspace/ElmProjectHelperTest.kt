package org.elm.workspace

import org.elm.TestProject
import org.elm.fileTree
import org.elm.openapiext.pathAsPath
import org.frawa.elmtest.core.ElmProjectTestsHelper
import java.util.*
import java.util.Optional.empty
import kotlin.streams.toList

class ElmProjectHelperTest : ElmWorkspaceTestBase() {

    fun `test all names`() {
        testProject()

        checkEquals(Arrays.asList("a", "b"), ElmProjectTestsHelper(project).allNames().toList())
    }

    fun `test by name`() {
        val testProject = testProject()
        val root = testProject.root.pathAsPath

        checkEquals(Optional.of(root.resolve("a").toString()), ElmProjectTestsHelper(project).projectDirPathByName("a"))
        checkEquals(Optional.of(root.resolve("b").toString()), ElmProjectTestsHelper(project).projectDirPathByName("b"))
        checkEquals(empty<String>(), ElmProjectTestsHelper(project).projectDirPathByName("gnu"))
        checkEquals(empty<String>(), ElmProjectTestsHelper(project).projectDirPathByName("without-tests"))
    }

    fun `test by path`() {
        val testProject = testProject()
        val root = testProject.root.pathAsPath

        checkEquals(Optional.of("a"), ElmProjectTestsHelper(project).nameByProjectDirPath(root.resolve("a").toString()))
        checkEquals(Optional.of("b"), ElmProjectTestsHelper(project).nameByProjectDirPath(root.resolve("b").toString()))
        checkEquals(empty<String>(), ElmProjectTestsHelper(project).nameByProjectDirPath(root.resolve("Toto").toString()))
    }

    fun `test elm project by path`() {
        val testProject = testProject()
        val root = testProject.root.pathAsPath

        checkEquals(
                Optional.of("a"),
                ElmProjectTestsHelper(project).elmProjectByProjectDirPath(root.resolve("a").toString())
                        .map(ElmProject::presentableName)
        )
        checkEquals(Optional.of("b"),
                ElmProjectTestsHelper(project).elmProjectByProjectDirPath(root.resolve("b").toString())
                        .map(ElmProject::presentableName)
        )
        checkEquals(empty<ElmProject>(), ElmProjectTestsHelper(project).elmProjectByProjectDirPath(root.resolve("Toto").toString()))
    }

    fun `test elm18 project`() {
        val testProject = testProject18()
        val root = testProject.root.pathAsPath

        checkEquals(
                Optional.of(true),
                ElmProjectTestsHelper(project).elmProjectByProjectDirPath(root.resolve("z").toString())
                        .map(ElmProject::isElm18)
        )
        checkEquals(empty<ElmProject>(),
                ElmProjectTestsHelper(project).elmProjectByProjectDirPath(root.resolve("z/tests").toString())
                        .map(ElmProject::isElm18)
        )
    }

    private fun testProject(): TestProject {
        val testProject = fileTree {
            dir("a") {
                project("elm.json", elmJson)
                dir("src") {
                    elm("Main.elm", "")
                }
                dir("tests") {
                }
            }
            dir("without-tests") {
                project("elm.json", elmJson)
                dir("src") {
                    elm("Main.elm", "")
                }
            }
            dir("b") {
                project("elm.json", elmJson)
                dir("src") {
                    elm("Main.elm", "")
                }
                dir("tests") {
                }
            }
        }.create(project, elmWorkspaceDirectory)

        val rootPath = testProject.root.pathAsPath
        project.elmWorkspace.apply {
            asyncAttachElmProject(rootPath.resolve("a/elm.json")).get()
            asyncAttachElmProject(rootPath.resolve("without-tests/elm.json")).get()
            asyncAttachElmProject(rootPath.resolve("b/elm.json")).get()
        }

        return testProject
    }

    val elmJson = """{
        "type": "application",
        "source-directories": [ "src" ],
        "elm-version": "0.19.0",
        "dependencies": {
            "direct": {
            },
            "indirect": {
            }
         },
        "test-dependencies": {
            "direct": {
            },
            "indirect": {
            }
        }
    }
    """

    private fun testProject18(): TestProject {
        val testProject = fileTree {
            dir("z") {
                project("elm-package.json", elmJson18)
                dir("src") {
                    elm("Main.elm", "")
                }
                dir("elm-stuff") {
                    file("exact-dependencies.json", "{}")
                }
                dir("tests") {
                    project("elm-package.json", elmJson18test)
                    elm("Test.elm", "")
                    dir("elm-stuff") {
                        file("exact-dependencies.json", "{}")
                    }
                }
            }
        }.create(project, elmWorkspaceDirectory)

        val rootPath = testProject.root.pathAsPath
        project.elmWorkspace.apply {
            asyncAttachElmProject(rootPath.resolve("z/elm-package.json")).get()
            asyncAttachElmProject(rootPath.resolve("z/tests/elm-package.json")).get()
        }

        return testProject
    }

    val elmJson18 = """{
        "version": "4.0.0",
        "repository": "https://github.com/user/project.git",
        "license": "BSD-3-Clause",
        "source-directories": [
            "src"
        ],
        "exposed-modules": [],
        "dependencies": {
        },
        "elm-version": "0.18.0 <= v < 0.19.0"
    }
    """

    val elmJson18test = """{
        "version": "4.0.0",
        "repository": "https://github.com/user/project.git",
        "license": "BSD-3-Clause",
        "source-directories": [
            "."
        ],
        "exposed-modules": [],
        "dependencies": {
        },
        "elm-version": "0.18.0 <= v < 0.19.0"
    }
    """
}
