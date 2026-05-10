(function () {
    "use strict";

    var root = document.documentElement;
    var originalDocumentTitle = document.title;
    var originalTextNodes = new WeakMap();
    var translatableAttributes = ["placeholder", "title", "aria-label"];
    var translationsAr = {
        "Fast home emergency services": "خدمات صيانة منزلية سريعة",
        "Login - FixNow": "تسجيل الدخول - FixNow",
        "Register - FixNow": "إنشاء حساب - FixNow",
        "Customer Dashboard - FixNow": "لوحة العميل - FixNow",
        "Technician Dashboard - FixNow": "لوحة الفني - FixNow",
        "Admin Dashboard - FixNow": "لوحة المشرف - FixNow",
        "Create Request - FixNow": "إنشاء طلب - FixNow",
        "Request Details - FixNow": "تفاصيل الطلب - FixNow",
        "Add Review - FixNow": "إضافة تقييم - FixNow",
        "Emergency services, clearly tracked.": "خدمات طوارئ منزلية بتتبع واضح.",
        "Spring Boot MVC view layer prepared for controller integration.": "واجهة Spring Boot MVC جاهزة للتكامل مع الكنترولر.",
        "FixNow Spring Boot MVC view layer.": "طبقة عرض FixNow باستخدام Spring Boot MVC.",
        "Modern view prepared for Spring MVC rendering.": "واجهة حديثة جاهزة للعرض عبر Spring MVC.",
        "Trust highlights": "نقاط الثقة",
        "Footer navigation": "تنقل التذييل",
        "Breadcrumb": "مسار الصفحة",
        "Current page": "الصفحة الحالية",
        "Login": "تسجيل الدخول",
        "Register": "إنشاء حساب",
        "Customer": "عميل",
        "Technician": "فني",
        "Admin": "مشرف",
        "Logout": "تسجيل الخروج",
        "Book service": "احجز خدمة",
        "Track request": "تتبع الطلب",
        "Light": "فاتح",
        "Dark": "داكن",
        "Menu": "القائمة",
        "Primary navigation": "التنقل الرئيسي",
        "Display preferences": "إعدادات العرض",
        "Language toggle": "تغيير اللغة",
        "Theme toggle": "تغيير الثيم",
        "Emergency help, without the chaos": "مساعدة طارئة بدون تعقيد",
        "Book a nearby technician and track every step.": "احجز فني قريب وتابع كل خطوة.",
        "FixNow gives customers one trusted place to request urgent home repairs, follow status, and review completed work.": "FixNow يوفر للعملاء مكانا موثوقا لطلب صيانة منزلية عاجلة، ومتابعة الحالة، وتقييم العمل بعد اكتماله.",
        "Fast matching": "توصيل سريع",
        "Prepared for technician availability data.": "جاهز لبيانات توفر الفنيين.",
        "Clear tracking": "تتبع واضح",
        "Customers can follow each request state.": "العملاء يقدروا يتابعوا حالة كل طلب.",
        "Service history": "سجل الخدمات",
        "Ready for future completed-request data.": "جاهز لبيانات الطلبات المكتملة لاحقا.",
        "Emergency request": "طلب طارئ",
        "Nearby technician": "فني قريب",
        "Track repair": "تتبع الإصلاح",
        "Account access": "الدخول للحساب",
        "Sign in to FixNow": "تسجيل الدخول إلى FixNow",
        "Use your account to open the correct dashboard.": "استخدم حسابك لفتح لوحة التحكم المناسبة.",
        "Invalid email or password.": "البريد الإلكتروني أو كلمة المرور غير صحيحة.",
        "Additional error detail from redirect.": "تفاصيل خطأ إضافية من التحويل.",
        "You have been logged out successfully.": "تم تسجيل الخروج بنجاح.",
        "Account created successfully. Please log in.": "تم إنشاء الحساب بنجاح. سجل الدخول الآن.",
        "Account created successfully": "تم إنشاء الحساب بنجاح",
        "Success": "نجاح",
        "Error": "خطأ",
        "Email address": "البريد الإلكتروني",
        "Password": "كلمة المرور",
        "Show": "إظهار",
        "Hide": "إخفاء",
        "New to FixNow?": "جديد على FixNow؟",
        "Create an account": "إنشاء حساب",
        "customer@example.com": "customer@example.com",
        "Enter your password": "اكتب كلمة المرور",
        "Join FixNow": "انضم إلى FixNow",
        "Choose how you want to use FixNow.": "اختر طريقة استخدامك لـ FixNow.",
        "Customers book and track repairs. Technicians receive jobs that match their service skill.": "العملاء يحجزون ويتابعون الإصلاحات. والفنيون يستقبلون طلبات مناسبة لتخصصهم.",
        "CUSTOMER": "عميل",
        "TECHNICIAN": "فني",
        "ADMIN": "مشرف",
        "Book and track services.": "احجز وتابع الخدمات.",
        "Accept assigned jobs.": "اقبل الأعمال المسندة إليك.",
        "Demo admins are created by the system seed.": "حسابات المشرف التجريبية يتم إنشاؤها من بيانات النظام.",
        "Register role": "اختيار نوع الحساب",
        "Open dashboard": "فتح لوحة التحكم",
        "Start workflow": "بدء الخطوات",
        "Create profile": "إنشاء ملف",
        "Select your role first": "اختر نوع الحساب أولا",
        "Choose the account type, then complete the form prepared for that role.": "اختر نوع الحساب، ثم أكمل الفورم الخاص به.",
        "Registration failed. Please check your details or use another email.": "فشل التسجيل. راجع بياناتك أو استخدم بريدا آخر.",
        "Account created successfully.": "تم إنشاء الحساب بنجاح.",
        "Choose registration role": "اختر نوع التسجيل",
        "Customer account": "حساب عميل",
        "Book home services and review completed work.": "احجز خدمات منزلية وقيم العمل المكتمل.",
        "Technician account": "حساب فني",
        "Set your service skill and receive matching jobs.": "حدد مهنتك واستقبل الطلبات المناسبة.",
        "Change role": "تغيير النوع",
        "Customer registration": "تسجيل عميل",
        "Create your booking account": "أنشئ حساب الحجز الخاص بك",
        "Full name": "الاسم الكامل",
        "Phone number": "رقم الهاتف",
        "Create a password": "أنشئ كلمة مرور",
        "Create customer account": "إنشاء حساب عميل",
        "Technician registration": "تسجيل فني",
        "Create your technician profile": "أنشئ ملفك كفني",
        "What is your profession?": "ما مهنتك؟",
        "Plumbing": "سباكة",
        "Leaks, sinks, drains, and pipes.": "تسريبات، أحواض، صرف، ومواسير.",
        "Electricity": "كهرباء",
        "Power, lighting, switches, sockets.": "كهرباء، إضاءة، مفاتيح، وبرايز.",
        "AC Repair": "صيانة تكييف",
        "Cooling, filters, and AC faults.": "تبريد، فلاتر، وأعطال التكييف.",
        "Locksmith": "أقفال ومفاتيح",
        "Locks, keys, and access support.": "أقفال، مفاتيح، ومساعدة في الدخول.",
        "Appliance Repair": "صيانة أجهزة",
        "Fridges, washers, and appliances.": "ثلاجات، غسالات، وأجهزة منزلية.",
        "General Maintenance": "صيانة عامة",
        "Mixed home repair support.": "دعم لإصلاحات منزلية متنوعة.",
        "Short work description": "وصف مختصر لعملك",
        "Write a short description about your experience, tools, service area, or the jobs you handle best.": "اكتب وصفا مختصرا عن خبرتك أو أدواتك أو منطقة خدمتك أو أفضل الأعمال التي تنفذها.",
        "Create technician account": "إنشاء حساب فني",
        "Already registered?": "لديك حساب بالفعل؟",
        "Go to login": "اذهب لتسجيل الدخول",
        "Your full name": "اسمك الكامل",
        "name@example.com": "name@example.com",
        "technician@example.com": "technician@example.com",
        "Admin operations": "عمليات المشرف",
        "Welcome,": "مرحبا،",
        "This dashboard is ready for real system metrics once controllers provide database-backed values.": "لوحة التحكم جاهزة لعرض مؤشرات فعلية من قاعدة البيانات.",
        "Awaiting system data": "بانتظار بيانات النظام",
        "Ready for dispatch": "جاهز للتوزيع",
        "Admin system metrics": "مؤشرات النظام للمشرف",
        "Admin management cards": "بطاقات إدارة المشرف",
        "Users": "المستخدمون",
        "Technicians": "الفنيون",
        "Requests": "الطلبات",
        "Reviews": "التقييمات",
        "User management": "إدارة المستخدمين",
        "Monitor registered users from the live database count.": "تابع عدد المستخدمين المسجلين من قاعدة البيانات.",
        "Refresh users": "تحديث المستخدمين",
        "Dispatch overview": "نظرة على التوزيع",
        "Assign pending customer requests to available technicians.": "اسند طلبات العملاء المعلقة للفنيين المتاحين.",
        "Refresh dispatch": "تحديث التوزيع",
        "Review quality": "جودة التقييمات",
        "Review count is connected to completed service feedback.": "عدد التقييمات مربوط بتعليقات الخدمات المكتملة.",
        "Refresh reviews": "تحديث التقييمات",
        "Dispatch queue": "قائمة التوزيع",
        "Pending requests": "الطلبات المعلقة",
        "No pending requests": "لا توجد طلبات معلقة",
        "New customer requests will appear here when they are ready for assignment.": "طلبات العملاء الجديدة ستظهر هنا عندما تكون جاهزة للإسناد.",
        "Service request": "طلب خدمة",
        "Service request created successfully. We will assign a matching technician soon.": "تم إنشاء طلب الخدمة بنجاح. سنسند فني مناسب قريبا.",
        "Customer home": "لوحة العميل",
        "Track requests, book a technician, and leave reviews after completed work.": "تابع الطلبات، واحجز فني، واترك تقييمك بعد اكتمال العمل.",
        "No recent status": "لا توجد حالة حديثة",
        "Pending": "معلق",
        "Active requests": "طلبات نشطة",
        "Completed": "مكتمل",
        "Ready to review": "جاهز للتقييم",
        "Technicians available": "فنيون متاحون",
        "New service request": "طلب خدمة جديد",
        "Describe the issue, location, and urgency.": "اكتب المشكلة والموقع ودرجة الاستعجال.",
        "Create request": "إنشاء طلب",
        "Review completed work": "تقييم العمل المكتمل",
        "Rate your technician after a completed job.": "قيّم الفني بعد اكتمال العمل.",
        "Completed jobs that are ready for review will appear here.": "الأعمال المكتملة الجاهزة للتقييم ستظهر هنا.",
        "Add review": "إضافة تقييم",
        "No reviews ready": "لا توجد تقييمات جاهزة",
        "Service requests": "طلبات الخدمة",
        "Your requests": "طلباتك",
        "No requests yet": "لا توجد طلبات بعد",
        "Create a request to see it listed here.": "أنشئ طلبا ليظهر هنا.",
        "Service": "خدمة",
        "Status": "الحالة",
        "Urgency": "الاستعجال",
        "Location": "الموقع",
        "Quick actions": "إجراءات سريعة",
        "View details": "عرض التفاصيل",
        "Book a Service": "احجز خدمة",
        "Book a service": "احجز خدمة",
        "Choose the repair type, describe the problem, and prepare the request for controller handling.": "اختر نوع الإصلاح، واشرح المشكلة، وجهز الطلب للمعالجة.",
        "Service booking platform": "منصة حجز الخدمات",
        "Step 1": "الخطوة 1",
        "Choose service category": "اختر نوع الخدمة",
        "Request status messages will appear here.": "رسائل حالة الطلب ستظهر هنا.",
        "Service category cards": "بطاقات أنواع الخدمات",
        "Leaks, sinks, drains": "تسريبات، أحواض، صرف",
        "Power, lighting, sockets": "كهرباء، إضاءة، برايز",
        "Cooling and filter issues": "مشاكل تبريد وفلاتر",
        "Locks and access support": "أقفال ومساعدة دخول",
        "AC, fridge, washer": "تكييف، ثلاجة، غسالة",
        "Building, street, city, or nearby landmark": "المبنى، الشارع، المدينة، أو علامة قريبة",
        "Select urgency": "اختر درجة الاستعجال",
        "Normal": "عادي",
        "Urgent": "عاجل",
        "Emergency": "طارئ",
        "Description": "الوصف",
        "Explain what happened, when it started, and any safety concern.": "اشرح ما حدث، ومتى بدأ، وأي مخاوف تتعلق بالسلامة.",
        "Submit request": "إرسال الطلب",
        "Back to dashboard": "العودة للوحة التحكم",
        "Booking flow": "خطوات الحجز",
        "How FixNow handles a request": "كيف يتعامل FixNow مع الطلب",
        "Choose": "اختيار",
        "Select the right service category.": "اختر نوع الخدمة المناسب.",
        "Describe": "وصف",
        "Add urgency, location, and details.": "أضف الاستعجال والموقع والتفاصيل.",
        "Track": "تتبع",
        "Status appears after controller integration.": "الحالة تظهر بعد ربط الكنترولر.",
        "Match": "مطابقة",
        "FixNow checks technician skill and availability.": "FixNow يفحص مهارة الفني وتوفره.",
        "Best for": "مناسب لـ",
        "Emergency home service booking": "حجز خدمات منزلية طارئة",
        "Current page role": "دور الصفحة الحالي",
        "Dispatch rule": "قاعدة الإسناد",
        "Category must match technician skill": "الفئة يجب أن تطابق مهارة الفني",
        "View state": "حالة العرض",
        "Ready for future controller data": "جاهز لبيانات الكنترولر لاحقا",
        "Rate Your Service": "قيّم خدمتك",
        "Submit feedback after a completed request.": "أرسل رأيك بعد اكتمال الطلب.",
        "Review form": "فورم التقييم",
        "Share your service experience": "شارك تجربتك مع الخدمة",
        "Service summary will appear here after a completed request is selected.": "ملخص الخدمة سيظهر هنا بعد اختيار طلب مكتمل.",
        "Review messages will appear here.": "رسائل التقييم ستظهر هنا.",
        "Rating": "التقييم",
        "Selected rating:": "التقييم المختار:",
        "Not selected": "لم يتم الاختيار",
        "Comment": "تعليق",
        "Tell us about timing, quality, communication, and trust.": "اكتب عن الالتزام بالوقت والجودة والتواصل والثقة.",
        "Submit review": "إرسال التقييم",
        "No completed service selected": "لم يتم اختيار خدمة مكتملة",
        "Navigate here from a completed request to submit your review.": "ادخل هنا من طلب مكتمل لإرسال تقييمك.",
        "Service details": "تفاصيل الخدمة",
        "Completed service": "خدمة مكتملة",
        "Request ID": "رقم الطلب",
        "Not available": "غير متاح",
        "Category": "الفئة",
        "Track Request": "تتبع الطلب",
        "Request information is neutral until controller data is available.": "معلومات الطلب تبقى عامة حتى تتوفر بيانات الكنترولر.",
        "Request details": "تفاصيل الطلب",
        "Request overview": "نظرة عامة على الطلب",
        "Current request": "الطلب الحالي",
        "Request not selected": "لم يتم اختيار طلب",
        "No description provided": "لا يوجد وصف",
        "Tracking": "التتبع",
        "Status timeline": "خط زمني للحالة",
        "No tracking updates yet": "لا توجد تحديثات تتبع بعد",
        "Tracking events will appear after the request is saved and assigned.": "أحداث التتبع ستظهر بعد حفظ الطلب وإسناده.",
        "Waiting for controller-provided request status.": "بانتظار حالة الطلب من الكنترولر.",
        "Request progress": "تقدم الطلب",
        "Request timeline": "الخط الزمني للطلب",
        "PENDING -> ASSIGNED -> ACCEPTED -> COMPLETED": "معلق -> مسند -> مقبول -> مكتمل",
        "Request submitted": "تم إرسال الطلب",
        "The customer request is saved and waiting for dispatch.": "تم حفظ طلب العميل وهو بانتظار الإسناد.",
        "Technician assigned": "تم إسناد الفني",
        "No matching available technician yet; admin can assign one later.": "لا يوجد فني مناسب متاح حاليا؛ يمكن للمشرف الإسناد لاحقا.",
        "Technician accepted": "قبل الفني الطلب",
        "The technician confirmed the job and is working on it.": "الفني أكد الطلب ويعمل عليه الآن.",
        "Job completed": "تم إنهاء العمل",
        "The service is done and ready for customer review.": "الخدمة انتهت وجاهزة لتقييم العميل.",
        "Waiting for assignment": "بانتظار الإسناد",
        "FixNow will match this request with an available technician who has the right skill.": "FixNow سيطابق هذا الطلب مع فني متاح لديه المهارة المناسبة.",
        "Waiting for technician acceptance": "بانتظار قبول الفني",
        "The assigned technician needs to accept the job before work starts.": "الفني المسند يحتاج قبول العمل قبل البدء.",
        "Technician is working": "الفني يعمل الآن",
        "The technician can mark this job complete after finishing the service.": "يمكن للفني إنهاء الطلب بعد إكمال الخدمة.",
        "The customer can now add one review for this completed request.": "يمكن للعميل الآن إضافة تقييم واحد لهذا الطلب المكتمل.",
        "Assigned technician": "الفني المسند",
        "Not assigned yet": "لم يتم الإسناد بعد",
        "Technician details will appear after assignment.": "تفاصيل الفني ستظهر بعد الإسناد.",
        "Distance": "المسافة",
        "Request summary": "ملخص الطلب",
        "Request number": "رقم الطلب",
        "Actions": "الإجراءات",
        "Next step": "الخطوة التالية",
        "Create a request first, then this page can display status and assignment data.": "أنشئ طلبا أولا، ثم ستعرض هذه الصفحة الحالة وبيانات الإسناد.",
        "Technician workspace": "مساحة عمل الفني",
        "Assigned jobs and availability controls will appear here once technician data is connected.": "الأعمال المسندة وحالة التوفر ستظهر هنا بعد ربط بيانات الفني.",
        "Availability": "التوفر",
        "Available": "متاح",
        "Busy": "مشغول",
        "Availability not set": "لم يتم تحديد التوفر",
        "Set availability from the technician controller later": "سيتم تحديد التوفر لاحقا من كنترولر الفني",
        "Go available": "اجعلني متاحا",
        "Go unavailable": "اجعلني غير متاح",
        "You are available for matching now.": "أنت متاح للمطابقة الآن.",
        "You are unavailable for new jobs now.": "أنت غير متاح للطلبات الجديدة الآن.",
        "Complete active jobs before becoming available": "أكمل الأعمال النشطة قبل أن تصبح متاحا",
        "Complete active jobs before becoming available again.": "أكمل الأعمال النشطة قبل أن تصبح متاحا مرة أخرى.",
        "Skill not set": "لم يتم تحديد المهنة",
        "Add a short description when registering as a technician.": "أضف وصفا مختصرا عند التسجيل كفني.",
        "Technician job statistics": "إحصائيات أعمال الفني",
        "Assigned jobs": "الأعمال المسندة",
        "Available requests": "الطلبات المتاحة",
        "Completed jobs": "الأعمال المكتملة",
        "No assigned jobs yet": "لا توجد أعمال مسندة بعد",
        "Your assigned jobs": "أعمالك المسندة",
        "Accepted and assigned jobs will appear in this area.": "الأعمال المقبولة والمسندة ستظهر هنا.",
        "Job queue": "قائمة الأعمال",
        "No open queue items yet": "لا توجد طلبات مفتوحة بعد",
        "Available customer requests": "طلبات العملاء المتاحة",
        "New pending customer requests will appear here.": "طلبات العملاء المعلقة الجديدة ستظهر هنا.",
        "Customer": "العميل",
        "Complete job": "إنهاء العمل",
        "Accept job": "قبول العمل",
        "Accept assigned job": "قبول العمل المسند",
        "Matched jobs arrive as assigned, then you accept them before completing the work.": "الأعمال المطابقة تصل كمسندة، ثم تقبلها قبل إنهاء العمل.",
        "No location provided": "لا يوجد موقع",
        "Not set": "غير محدد",
        "No available technicians right now.": "لا يوجد فنيون متاحون حاليا.",
        "No matching available technicians right now.": "لا يوجد فنيون مناسبون متاحون حاليا.",
        "Assign technician": "إسناد فني",
        "403 - Access Denied": "403 - الوصول مرفوض",
        "404 - Not Found": "404 - غير موجود",
        "404 - Page Not Found": "404 - الصفحة غير موجودة",
        "500 - Server Error": "500 - خطأ في السيرفر",
        "You don't have permission to access this page.": "ليس لديك صلاحية للوصول لهذه الصفحة.",
        "The page you are looking for does not exist.": "الصفحة التي تبحث عنها غير موجودة.",
        "Something went wrong on the server.": "حدث خطأ في السيرفر.",
        "Go Home": "العودة للرئيسية",
        "NEW": "جديد",
        "TECH": "فني",
        "PENDING": "معلق",
        "ACCEPTED": "مقبول",
        "ASSIGNED": "مسند",
        "COMPLETED": "مكتمل"
    };

    function setPressed(buttons, activeValue, attribute) {
        buttons.forEach(function (button) {
            button.setAttribute("aria-pressed", String(button.getAttribute(attribute) === activeValue));
        });
    }

    function normalizeText(value) {
        return String(value || "").replace(/\s+/g, " ").trim();
    }

    function lookupTranslation(value, language) {
        var original = normalizeText(value);
        if (language !== "ar" || !original) {
            return null;
        }
        if (translationsAr[original]) {
            return translationsAr[original];
        }
        if (/^Assign\s+/.test(original)) {
            return original.replace(/^Assign\s+/, "إسناد ");
        }
        if (/^Matched with\s+(.+)\s+for this service category\.$/.test(original)) {
            return original.replace(/^Matched with\s+(.+)\s+for this service category\.$/, "تمت المطابقة مع $1 لهذه الفئة.");
        }
        if (/^Service request created and auto-matched with\s+(.+)\.$/.test(original)) {
            return original.replace(/^Service request created and auto-matched with\s+(.+)\.$/, "تم إنشاء طلب الخدمة ومطابقته تلقائيا مع $1.");
        }
        if (/^Request\s+#/.test(original)) {
            return original.replace(/^Request\s+#/, "طلب #");
        }
        if (/^Rating:\s*/.test(original)) {
            return original.replace(/^Rating:\s*/, "التقييم: ");
        }
        return null;
    }

    function translate(value, language) {
        return lookupTranslation(value, language) || value;
    }

    function localizeTextNode(node, language) {
        var original = originalTextNodes.get(node);
        if (original === undefined) {
            original = node.nodeValue;
            originalTextNodes.set(node, original);
        }

        var normalized = normalizeText(original);
        if (!normalized) {
            node.nodeValue = original;
            return;
        }

        if (language !== "ar") {
            node.nodeValue = original;
            return;
        }

        var translated = lookupTranslation(normalized, language);
        if (!translated) {
            node.nodeValue = original;
            return;
        }

        var leading = original.match(/^\s*/)[0];
        var trailing = original.match(/\s*$/)[0];
        node.nodeValue = leading + translated + trailing;
    }

    function localizeAttributes(language) {
        Array.prototype.slice.call(document.querySelectorAll("*")).forEach(function (element) {
            translatableAttributes.forEach(function (attribute) {
                if (!element.hasAttribute(attribute)) {
                    return;
                }

                var originalAttribute = "data-i18n-original-" + attribute;
                if (!element.hasAttribute(originalAttribute)) {
                    element.setAttribute(originalAttribute, element.getAttribute(attribute));
                }

                var original = element.getAttribute(originalAttribute);
                element.setAttribute(attribute, language === "ar" ? translate(original, language) : original);
            });
        });
    }

    function applyTranslations(language) {
        var walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, {
            acceptNode: function (node) {
                if (!node.parentElement || node.parentElement.closest("script, style, noscript")) {
                    return NodeFilter.FILTER_REJECT;
                }
                return normalizeText(node.nodeValue) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT;
            }
        });

        var node = walker.nextNode();
        while (node) {
            localizeTextNode(node, language);
            node = walker.nextNode();
        }

        localizeAttributes(language);
        document.title = language === "ar" ? translate(originalDocumentTitle, language) : originalDocumentTitle;
        refreshRatingOutputs(language);
    }

    function setupMobileNavigation() {
        var toggle = document.querySelector("[data-nav-toggle]");
        var nav = document.querySelector("[data-primary-nav]");

        if (!toggle || !nav) {
            return;
        }

        toggle.addEventListener("click", function () {
            var isOpen = nav.classList.toggle("is-open");
            toggle.setAttribute("aria-expanded", String(isOpen));
        });
    }

    function setupThemeToggle() {
        var buttons = Array.prototype.slice.call(document.querySelectorAll("[data-theme-choice]"));
        var savedTheme = localStorage.getItem("fixnow-theme") || "light";

        root.setAttribute("data-theme", savedTheme);
        setPressed(buttons, savedTheme, "data-theme-choice");

        buttons.forEach(function (button) {
            button.addEventListener("click", function () {
                var theme = button.getAttribute("data-theme-choice");
                root.setAttribute("data-theme", theme);
                localStorage.setItem("fixnow-theme", theme);
                setPressed(buttons, theme, "data-theme-choice");
            });
        });
    }

    function setupLanguageToggle() {
        var buttons = Array.prototype.slice.call(document.querySelectorAll("[data-lang-choice]"));
        var savedLanguage = localStorage.getItem("fixnow-language") || "en";

        root.setAttribute("lang", savedLanguage);
        root.setAttribute("dir", savedLanguage === "ar" ? "rtl" : "ltr");
        root.setAttribute("data-language", savedLanguage);
        setPressed(buttons, savedLanguage, "data-lang-choice");
        applyTranslations(savedLanguage);

        buttons.forEach(function (button) {
            button.addEventListener("click", function () {
                var language = button.getAttribute("data-lang-choice");
                root.setAttribute("lang", language);
                root.setAttribute("dir", language === "ar" ? "rtl" : "ltr");
                root.setAttribute("data-language", language);
                localStorage.setItem("fixnow-language", language);
                setPressed(buttons, language, "data-lang-choice");
                applyTranslations(language);
            });
        });
    }

    function setupRatingOutput() {
        var inputs = Array.prototype.slice.call(document.querySelectorAll("[data-rating-input]"));

        if (inputs.length === 0) {
            return;
        }

        inputs.forEach(function (input) {
            input.addEventListener("change", function () {
                refreshRatingOutputs(root.getAttribute("data-language"));
            });
        });
        refreshRatingOutputs(root.getAttribute("data-language"));
    }

    function refreshRatingOutputs(language) {
        Array.prototype.slice.call(document.querySelectorAll("[data-rating-output]")).forEach(function (output) {
            var selected = document.querySelector("[data-rating-input]:checked");
            if (!selected) {
                output.textContent = translate("Not selected", language);
                return;
            }
            output.textContent = language === "ar" ? selected.value + " من 5" : selected.value + " / 5";
        });
    }

    function setupPasswordReveal() {
        document.querySelectorAll("[data-password-toggle]").forEach(function (button) {
            var target = document.querySelector(button.getAttribute("data-password-toggle"));

            if (!target) {
                return;
            }

            button.addEventListener("click", function () {
                var showPassword = target.type === "password";
                target.type = showPassword ? "text" : "password";
                button.textContent = translate(showPassword ? "Hide" : "Show", root.getAttribute("data-language"));
            });
        });
    }

    function setupRegisterRoleChoice() {
        var chooser = document.querySelector("[data-register-role-chooser]");
        var roleButtons = Array.prototype.slice.call(document.querySelectorAll("[data-register-role]"));
        var panels = Array.prototype.slice.call(document.querySelectorAll("[data-register-panel]"));
        var backButtons = Array.prototype.slice.call(document.querySelectorAll("[data-register-back]"));

        if (!chooser || roleButtons.length === 0 || panels.length === 0) {
            return;
        }

        function setPanelControls(panel, disabled) {
            Array.prototype.slice.call(panel.querySelectorAll("input, select, textarea, button")).forEach(function (control) {
                if (control.hasAttribute("data-register-back")) {
                    control.disabled = false;
                    return;
                }
                control.disabled = disabled;
            });
        }

        function showChooser() {
            chooser.hidden = false;
            roleButtons.forEach(function (button) {
                button.setAttribute("aria-pressed", "false");
            });
            panels.forEach(function (panel) {
                panel.hidden = true;
                setPanelControls(panel, true);
            });
        }

        function showPanel(role) {
            chooser.hidden = true;
            roleButtons.forEach(function (button) {
                button.setAttribute("aria-pressed", String(button.getAttribute("data-register-role") === role));
            });
            panels.forEach(function (panel) {
                var isSelected = panel.getAttribute("data-register-panel") === role;
                panel.hidden = !isSelected;
                setPanelControls(panel, !isSelected);
            });
        }

        roleButtons.forEach(function (button) {
            button.setAttribute("aria-pressed", "false");
            button.addEventListener("click", function () {
                showPanel(button.getAttribute("data-register-role"));
            });
        });

        backButtons.forEach(function (button) {
            button.addEventListener("click", showChooser);
        });

        var initialRole = new URLSearchParams(window.location.search).get("role");
        if (initialRole) {
            showPanel(initialRole.toUpperCase());
        } else {
            showChooser();
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupMobileNavigation();
        setupThemeToggle();
        setupLanguageToggle();
        setupRatingOutput();
        setupPasswordReveal();
        setupRegisterRoleChoice();
    });
}());
