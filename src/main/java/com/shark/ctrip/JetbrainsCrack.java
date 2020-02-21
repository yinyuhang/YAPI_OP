package com.shark.ctrip;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;

/**
 * 博客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 更多原处优质干货
 * Agent 类，所有程序启动只要配置了 -javaagent: 都会走到 premain 方法
 */
@Slf4j
public class JetbrainsCrack {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("**************************************");
        System.out.println("*       公众号：bugstack虫洞栈       *");
        System.out.println("*     博客：https://bugstack.cn      *");
        System.out.println("*   你用剑，我用刀，好的代码都很烧！ *");
        System.out.println("**************************************");
        inst.addTransformer(new MethodEntryTransformer());
    }

    static class MethodEntryTransformer implements ClassFileTransformer {

        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer)
                throws IllegalClassFormatException {
            try {
//                log.info("{}", className);
                if (className.equals("com/shark/ctrip/DecodeCertificates")) {

                    try {
                        String targetClz = className.replace("/", ".");
                        log.info(targetClz);
                        Class<?> aClass = Class.forName(targetClz);
                        log.info("{}", aClass);
                        if (aClass != null)
                            log.info("{}", aClass.getCanonicalName());
                    } catch (Error e) {
                        log.error("", e);
                    }

                    ClassReader cr = new ClassReader(classfileBuffer);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, 0);
                    List<MethodNode> methodNodes = cn.methods;
                    for (MethodNode methodNode : methodNodes) {
                        if ("decodeLicense".equals(methodNode.name)) {
                            InsnList insns = methodNode.instructions;
                            //清除指令
                            insns.clear();
                            insns.add(new VarInsnNode(Opcodes.ALOAD, 1)); // 将本地指定的引用存入栈中
                            insns.add(new InsnNode(Opcodes.ARETURN));          // 从方法中返回引用类型的数据
                            // 访问结束
                            methodNode.visitEnd();
                            ClassWriter cw = new ClassWriter(0);
                            cn.accept(cw);
                            byte[] bytes = cw.toByteArray();
                            // 输出字节码到Class
                            this.outputClazz("aa", bytes);
                            // 返回最新字节码
                            return cw.toByteArray();
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
                return classfileBuffer;
            }

            return classfileBuffer;
        }

        private void outputClazz(String clzName, byte[] bytes) {
            // 输出类字节码
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(clzName);
                log.info("ASM类输出路径：{}", (new File("")).getAbsolutePath());
                out.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != out) try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

