/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.demo.springboot.provider;
import net.demo.springboot.services.UserManager;
import net.example.domain.consumer.UserService;
import net.example.domain.domain.UserDO;
import net.hasor.core.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 服务实现
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
@Component()
public class UserServiceImpl implements UserService {
    @Autowired
    private UserManager userManager;
    @Override
    public List<UserDO> queryUser() {
        try {
            List<UserDO> userDOs = userManager.queryList();
            return userDOs;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}