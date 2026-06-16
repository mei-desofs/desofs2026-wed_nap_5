# Changelog

## [2.2.0](https://github.com/mei-desofs/desofs2026-wed_nap_5/compare/v2.1.0...v2.2.0) (2026-06-16)


### Features

* **frontend security:** Integrated the FetchMetadataFilter into the SecurityConfig filter chain, ensuring it runs early to protect all endpoints. ([77a88b7](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/77a88b72faa2ff3660297cdaea33422222e0a631))


### Bug Fixes

* **db:** align migrations with string identifiers ([2d8ef2c](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/2d8ef2ced9ab0dd9ce064f5cec7f2fb228682a67))

## [2.1.0](https://github.com/mei-desofs/desofs2026-wed_nap_5/compare/v2.0.0...v2.1.0) (2026-06-14)


### Features

* adds logging to JwtFilter ([4db3782](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/4db3782ed5ae6cafafee6b80b736d74116558c0e))
* adds more chatroom and user endpoints and correct related code. updates psotamn collection ([5f0069b](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5f0069b7a48e9c16c687ab0867f2eafe4fb237dc))
* adds postman collection ([d34889b](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/d34889bc5197174859e0aa3a94e0b05efbe08608))
* **ci:** implement unified CI/CD pipeline and restore old workflows ([ef4dfda](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/ef4dfda4333958039e12fd6b340a83786b476325))
* improves and adds more logging ([c3420a5](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/c3420a5a23e1e7856116808380d82008e7412fb2))
* **logging:** audit user lifecycle events ([a87357b](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/a87357b96a07c832466ef257906a44e41dfba090))
* **pipeline:** adding parallel jobs and dast security testing ([07f9bf7](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/07f9bf7b27f08eeef6f8207c2c5572beab1b168b))
* **pipeline:** adding runtime security tests with newman ([9e52134](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/9e521349f44104dc3c760da2c1949d865c35c6ba))
* updates bootstrap to create more data on start ([207b989](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/207b989d6b44b344907e598faec81fd7daa3a775))
* updates chat related endpoints ([1e863d0](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1e863d0b72fbf2aebb7d7e8acd06608816e886ca))
* updates CourseController and EnrollementService ([fbcaa5d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/fbcaa5df9d05f8f701e7f72331cd446ccc83b6fb))
* updates postman collection ([4e0a136](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/4e0a13692e3162fb45dafebee218ee9b949ee94e))
* **user:** add profile and account deactivation endpoints ([3594639](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/3594639865ae7ae101e0092c2de4a9521de6e773))


### Bug Fixes

* Added fix to assignment and submission entities ([65bcdc4](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/65bcdc414090eac3e387cf663d8866fd3e175e1b))
* adding report actions for owasp dependency in pipeline ([591da95](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/591da956e0512361c976204684075dacbf85ded4))
* app properties for testing ([37bf9b3](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/37bf9b335fd40afef2aee186133438303ecfab5c))
* application properties test ([5d52085](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5d52085144cdbc3fc6f833ae71b2b7a6c1a6c8c7))
* Assignment ID added to AuditLog ([5321824](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5321824f47c38c2acdc32a27211f27bbf61b109e))
* Assignment Test ([117e6a5](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/117e6a5a9e3bb08ff9030da643fb28849630ad5c))
* AssignmentControllerIntegration Test ([5f04d13](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5f04d1341dae18f66f67b6d3e40cc6708a1f5aa4))
* Change app properties of tests ([4ae7b5a](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/4ae7b5ab0047cd913e9905cedd1e138b071aa243))
* changing Test ([0d83dac](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0d83dac67d8df3bbfeb06f21a2442db4ae85df41))
* **chat:** handle missing chat room validation correctly ([915b45f](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/915b45f54cdd70f35e54c8fb703748fde71b9c7e))
* code corrections to unsure consistence ([9b381df](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/9b381dfc6c7890e379bd259c799e7b4e2e233546))
* corrects dependencies ([0a1d66e](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0a1d66e628766cf98b3f93056cb55a7e61d52023))
* corrects folder names ([b3361d3](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/b3361d360f1078a20af4f8d529134a8f987bbda8))
* corrects ids used ([52c9db1](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/52c9db16d85d66ea2475f257e417c839151691d8))
* corrects path for uploading files in postamn collection ([aaf80fd](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/aaf80fd0c5c031f89506b248d4e3c3948b6439ff))
* corrects postamn collection ([92ac53d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/92ac53d172c9b85ac50df03f9a77898e419ffb22))
* corrects target branches on unified pipeline ([1959781](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1959781a5f8d10451e12e090ac2483b208275c63))
* corrects target branches on unified pipeline ([1c73e47](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1c73e4730832186f2196bda28485a145610094b0))
* fix login and bootstrap password information ([0de5976](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0de5976e6b67e29bf1cc2e4c88782241886906c6))
* fix pipeline after merge from master ([c99410a](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/c99410a4e409fe7afe2657b02f9cb236c6fe648b))
* fixes pipeline ([9e3f902](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/9e3f90281f2e7bd4ca14c784541450653a07c305))
* fixes release-please pipeline ([061e5b4](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/061e5b401ffe37cfd04e13bd8b8f172fca073c97))
* Ids generated with CSPRNG ([81b25ad](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/81b25adf681b6abbc19eb126b80d3d24767fbff6))
* ipdates owasp dependency ([1bffd57](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1bffd5725b84519cf46548003f5497e1d724450e))
* makes changes for codeQL suggestions ([e5e0e32](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/e5e0e32ad608ddcfd9cb3dd99899dd06af3a75ab))
* makes changes for codeQL suggestions ([7f83f46](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/7f83f466bd88956a3572f1a3947212737aed25e1))
* reverts changes to UserController and SecurityConfig ([db4917f](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/db4917f71257d3411946b35af2749ad7a99d3dd8))
* **security:** harden JWT filter validation flow ([0f888a1](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0f888a13249b540b2a3676531de8044ed9bf8996))
* updates owasp in pipeline ([3ef2e6a](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/3ef2e6ab2a6acc651a7f6ce0bf4959af17c85314))
* updates owasp in pipeline ([1ee9293](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1ee9293c9abc21c347a948d37a7e9795152f19b8))
* updates unified pipeline and corrects dependencies ([af4ab44](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/af4ab4417f504d0e01753cb1c778f686a2ea6900))

## [2.0.0](https://github.com/mei-desofs/desofs2026-wed_nap_5/compare/v1.1.1...v2.0.0) (2026-05-18)


### ⚠ BREAKING CHANGES

* Changed ASVS

### Features

* Changed ASVS ([97cb955](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/97cb955f94362691cf9bae533c5a67cf4f5226bb))

## [1.1.1](https://github.com/mei-desofs/desofs2026-wed_nap_5/compare/v1.1.0...v1.1.1) (2026-05-18)


### Bug Fixes

* fix code error ([6401416](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/64014160ecb19c275e0088b47387c83eb746b2e0))
* fix not needed code ([fa0f749](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/fa0f749e8b1c20b1cd71a86953a1010c787e86e5))

## [1.1.0](https://github.com/mei-desofs/desofs2026-wed_nap_5/compare/v1.0.0...v1.1.0) (2026-05-18)


### Features

* cleans code ([3647977](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/36479772e906b87c4a1bda3f756f85d99c8ec3c2))

## 1.0.0 (2026-05-18)


### Features

* add access control for submissions and enhance exception handling ([75b8233](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/75b82330d9259653e5ae1999dc6d633f9099e328))
* add ChatRoomRepository to integration tests and clear data in setup ([10a67d2](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/10a67d292f35c9dfd9c180cd645b2bb629f15e3e))
* add file management threat model (dfd + report) ([a192bae](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/a192bae4a9b4b73f2e23234da4b68c8dbdbd0c41))
* add global exception handling and error response DTO; enhance submission service with validation and security checks ([1f5db1f](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1f5db1fd9ae9d32022823c5befa06f39abaeee71))
* Added tests to improve Mutation Test coverage ([34a9dac](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/34a9dac1f50b6b7dfcb7a48b7cb70658cf7a529c))
* adds Assignment and Submission classes with business logic ([58b7772](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/58b7772290fddc13bbd5179edc6f92ad32d13c5a))
* adds build workflow ([5466b56](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5466b56de1cf2e1e4e3c6dfb6ba2aeb2ed127a55))
* adds chat room related classes ([6f38ea6](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/6f38ea6ef32980fc88b67cb5bb0651d39a56278e))
* adds dast workflow ([bed9ea0](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/bed9ea0f37d73b5eee62f3b7ee74f88c812551d1))
* adds enrollement related classes ([daf4099](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/daf40993607b2962484ce7744f9257015ff3823d))
* adds more workflows ([9dc6609](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/9dc660927253338ecb4dc86cce0e8c31bcfab757))
* adds pit test workflow ([261e6a6](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/261e6a61068f9e1d8919f628780edf14d233e4bc))
* adds pit test workflow ([e9732b4](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/e9732b4e7e623b233c15442fe845eb0c7cf8ebef))
* adds release workflow ([bb9f7a7](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/bb9f7a7b56053f52223aba582f3302bc04d26152))
* adds security classes ([ae33c19](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/ae33c19e4b93842682286d3b9a7e208e4e748977))
* AuthController and  integration Tests to Resources and Courses ([b7b79a7](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/b7b79a731802aa475094428091ce9d308dfdd5e4))
* **auth:** implement JWT authentication and secure user responses ([d8c73d5](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/d8c73d58d784448f473084a2df6fda1eadf25a02))
* Bootstrap added ([cd872e6](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/cd872e6bb8f884132f6728e020c7b4088e09b3ef))
* Bootstrap adicionado ([dfe06b6](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/dfe06b67b4e83fcbf55ea12bc21365375805b850))
* Course Aggregate ([02e550d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/02e550df5981b4e22f49c6edf40e724c21c01e0f))
* **dfd:** add chat and file management Level 2 DFD STRIDE diagrams and update visuals ([d82bd02](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/d82bd028322d157d0e1a77aef7a25d18599f4457))
* implement assignment and submission management with audit logging ([e4f57b2](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/e4f57b2ffce38db9fae27309079e8c360c022c0a))
* initial project setup with spring boot, domain structure, health endpoint and README ([8a75f64](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/8a75f64c90d0f7bf390c3bd7e146754cfb020953))
* initial spring boot setup with domain structure and health endpoint ([f72b746](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f72b74660fefdde7b458d2797deba3f3b470193b))
* refactor assignment and submission models to use UUID for user identifiers; implement controllers and services for assignment and submission management ([cb43f07](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/cb43f07525133e8488f6de7b01f7d508a6ae5ae1))
* **security:** add chat management threat model (DFD + STRIDE) ([3956793](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/3956793083a9c2868ceaba0e5fb6216c83aa9d6e))
* updates chat room related classes ([b658352](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/b6583523214c449d3dd0365295b8fe72e9eb2898))
* updates controllers ([df53e7a](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/df53e7a4d70484826f514999e859deecffc12e78))
* updates pom.xml ([8dd2ce1](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/8dd2ce123c63dd0f000da36b3bdf9a2c7518aee8))
* **user:** integrate user aggregate into sprint architecture ([0191406](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0191406a77f17ae07a9b26757d7c1aa795659fc3))


### Bug Fixes

* add chat migrations and align enrollment schema ([43bc154](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/43bc15445a03e414470e492a55d38230b727d566))
* add enrollment active column migration ([777e9ac](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/777e9ac3d04b185926b413e6aa5ae50a6d758f8a))
* Add new security requirements for session management ([7fa865f](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/7fa865fdf0384de9e3f55365b26ae2e2ebfcd005))
* add yml to correct application start-up on dast workflow ([15b6539](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/15b6539b3659d4001f0ec6909347f2dc6ab813f9))
* added annonation to fix pipeline ([f12aa91](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f12aa91f3dc6e976db6a4f4e381918f2c08ca977))
* Added csrf to tests ([104d766](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/104d7669a2b7fbd4e9313527b60b32d9682597b3))
* Added fix names for Courses and Enrollments db tables ([f43ba2b](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f43ba2b1ba8573533420d79e4770fc255b438bf8))
* adds current pipeline to build workflow ([c94d977](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/c94d97740352a589aa0ae5b497818fab1a9fc0ee))
* Aditional Testing and fixed some permissions ([434086e](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/434086e7295c434cf7709104e5a513407b5c4654))
* code review suggestion ([0f39729](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/0f39729a360af5458af139833b2cafa8e1c4ce59))
* corrects build workflow ([8857747](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/8857747e3d1f2f5f598401f81a33549894702216))
* corrects build workflow ([3d7c564](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/3d7c5641ffe0f7cd9ee3a7f1af092e3a5bd63ba5))
* corrects chat tests ([dff29c1](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/dff29c107afd41a1f6292e6b4d4af4c7c60856a5))
* corrects classes names and formats code ([f6d9008](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f6d9008df5e2c71cc806c04680a7ecfb55a07970))
* corrects dast workflow ([6b4b4f9](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/6b4b4f981a496174758384a5a420f265e047bcce))
* corrects pom and secret and sca workflows ([a4b2ff9](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/a4b2ff9499ad1a1a92cfb914afaad353054080da))
* corrects pom and secret workflow ([178e947](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/178e947f62476a0c4b37a7e957fde7f6d70b1d5e))
* corrects quality errors ([b9a5400](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/b9a54003cd55c458d21c2f7359771ca0f76f12c2))
* corrects secrects ([20a2866](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/20a28661afd10fdd2eaa9880f599d2a234050692))
* corrects workflow ([340970c](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/340970c3c0b212d01d53b3aee71d1238213458f7))
* corrects workflow port ([748a74d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/748a74dfc5319b1825835f0e5fb6db8ae2a6b6cd))
* corrects workflows to correct fails ([65eb3ed](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/65eb3ed32c798391f98d409b244a21a8a30901ff))
* corrigir queries JPQL e tipos UUID nos repositories ([3d176fd](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/3d176fd673d0ce3d86c34ef651e681625c563f15))
* database ([e690ebe](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/e690ebe8c06441fc68db1d5b6a19e8515b5b6cfc))
* deleted files ([9d83847](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/9d838479029533cdbb51f8e2473b6c9e7f454100))
* DFDs level 1 ([eaa7a70](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/eaa7a7069a6771af4b20d443b48811253571ff44))
* DFDs level 1 ([b1b37ef](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/b1b37ef071191f37d8e3575b211110b0dd968fbf))
* formats code to correct workflow run ([871849d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/871849dbb6424d2b7431404397f4b33d23780988))
* functional req added to global ([1b024b2](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/1b024b2183314fe85a7391f13b759b4621cc3b26))
* integrations test fixed ([1397987](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/13979876145ca2e2710276cb825319b921600dbd))
* more integrations tests fixed ([7fa8b95](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/7fa8b950eb71d31cb667b40e3ebc12daff806c0c))
* pit test now working ([5bd0b15](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/5bd0b155eb05b70a3386adbeeda8e2e3ad0ef98c))
* program runs ([404ee69](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/404ee69e656125febde00f8797044f6aad3c0e83))
* removed test ([ace3573](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/ace35738cad2da1c5a660cce30f97056c0f280f8))
* removes unnecessary code for workflow ([c78ea51](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/c78ea5135521a84ef770e110d6fb43e04c5ae1c4))
* resource test ([f03b0bb](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f03b0bb845b4edb14082a35ed5fd72748d2f5b08))
* some IDs were fixed ([a47aa82](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/a47aa829e8dd30ff51ad4957adf0e6aaab0f2bb1))
* test ([2d3cd31](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/2d3cd31e737579fc2b47102d6955a4daed77ec8a))
* test changed to fix pipeline ([bd84c8d](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/bd84c8df45d772b7a8f1b269259e3fa71d996827))
* trying to fix pipeline ([dfd5664](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/dfd56648040da8fbeeb9f4abb3186a999e172db8))
* update DFD image filenames for consistency and clarity ([f503111](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/f503111a52e629441596479193838af9327f6491))
* updates dependencies ([bc49ae7](https://github.com/mei-desofs/desofs2026-wed_nap_5/commit/bc49ae7ddfdef1f868fa312a616c9a0dc6e7d7fc))
