Summary:  JSS Connector for Apache Tomcat, a JSSE module for Apache Tomcat that uses JSS
URL:      http://pki.fedoraproject.org/
License:  LGPLv2+
Group:    System Environment/Libraries

BuildArch:      noarch
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-buildroot

Source0:  http://pki.fedoraproject.org/pki/sources/%{name}/%{name}-%{version}.tar.gz

# jpackage-utils requires versioning to meet both build and runtime requirements
# jss requires versioning to meet both build and runtime requirements
# tomcat requires versioning to meet both build and runtime requirements
Conflicts:        pki-base < 10.4.0
BuildRequires:    ant
BuildRequires:    apache-commons-lang
BuildRequires:    java-devel
BuildRequires:    jpackage-utils >= 0:1.7.5-15
%if 0%{?fedora}
BuildRequires:    jss >= 4.4.2-2
%else
BuildRequires:    jss >= 4.4.0-7
%endif
%if 0%{?fedora} >= 23
BuildRequires:    tomcat >= 8.0.18
%else
BuildRequires:    tomcat >= 7.0.68
%endif

Requires:         apache-commons-lang
%if 0%{?fedora} >= 21
Requires:         java-headless
%else
Requires:         java
%endif
Requires:         jpackage-utils >= 0:1.7.5-15
%if 0%{?fedora}
Requires:         jss >= 4.4.2-2
%else
Requires:         jss >= 4.4.0-7
%endif
%if 0%{?fedora} >= 23
Requires:         tomcat >= 8.0.18
%else
Requires:         tomcat >= 7.0.68
%endif

# The 'tomcatjss' package conflicts with the 'tomcat-native' package
# because it uses an underlying NSS security model rather than the
# OpenSSL security model, so these two packages may not co-exist.
# (see Bugzilla Bug #441974 for details)
Conflicts:        tomcat-native

%if 0%{?rhel}
# For EPEL, override the '_sharedstatedir' macro on RHEL
%define           _sharedstatedir    /var/lib
%endif

%description
JSS Connector for Apache Tomcat, installed via the tomcatjss package,
is a Java Secure Socket Extension (JSSE) module for Apache Tomcat that
uses Java Security Services (JSS), a Java interface to Network Security
Services (NSS).

NOTE:  The 'tomcatjss' package conflicts with the 'tomcat-native' package
       because it uses an underlying NSS security model rather than the
       OpenSSL security model, so these two packages may not co-exist.

%prep

%setup -q
chmod -c -x LICENSE README

%build

ant -f build.xml -Dversion=%{version} -Djnidir=%{_jnidir}
ant -f build.xml -Dversion=%{version} -Djnidir=%{_jnidir} dist

%install
rm -rf %{buildroot}

# Unpack the files we just built
cd dist/binary
unzip %{name}-%{version}.zip -d %{buildroot}

# Install our files
cd %{buildroot}%{_javadir}
%if 0%{?rhel} || 0%{?fedora} < 21
mv %{name}.jar %{name}-%{version}.jar
ln -s %{name}-%{version}.jar %{name}.jar
%endif

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root)
%doc README LICENSE
%{_javadir}/*
